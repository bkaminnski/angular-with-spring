package com.softwarewithpassion.nrgyinvoicr.backend.meters.control;

import com.softwarewithpassion.nrgyinvoicr.backend.clients.entity.Client;
import com.softwarewithpassion.nrgyinvoicr.backend.meters.entity.Meter;
import com.softwarewithpassion.nrgyinvoicr.backend.meters.entity.MetersSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.softwarewithpassion.nrgyinvoicr.backend.meters.entity.MetersSpecification.onlyUnassigned;
import static com.softwarewithpassion.nrgyinvoicr.backend.meters.entity.MetersSpecification.serialNumberLike;

@Service
public class MetersService {
    private final MetersRepository metersRepository;

    MetersService(MetersRepository metersRepository) {
        this.metersRepository = metersRepository;
    }

    public Meter createMeter(Meter meter) throws MeterAlreadyRegisteredException {
        meter.setId(null);
        if (metersRepository.findBySerialNumber(meter.getSerialNumber()).isPresent()) {
            throw new MeterAlreadyRegisteredException(meter.getSerialNumber());
        }
        return metersRepository.save(meter);
    }

    public Optional<Meter> getMeter(Long id) {
        return metersRepository.findById(id);
    }

    public Page<Meter> findMeters(MetersSearchCriteria criteria) {
        String serialNumber = criteria.getSerialNumber();
        Specification<Meter> specification = serialNumberLike(serialNumber)
                .and(onlyUnassigned(criteria.isOnlyUnassigned()));
        return metersRepository.findAll(specification, criteria.getPageDefinition().asPageRequest());
    }

    public Optional<Meter> updateMeter(Meter meter) throws MeterAlreadyRegisteredException {
        if (metersRepository.findBySerialNumberAndIdNot(meter.getSerialNumber(), meter.getId()).isPresent()) {
            throw new MeterAlreadyRegisteredException(meter.getSerialNumber());
        }
        return metersRepository
                .findById(meter.getId())
                .map(m -> meter.withClient(m.getClient()))
                .map(metersRepository::save);
    }

    public Client toClientWithAssignedMeter(Client client, Meter meter) {
        Meter assigned = metersRepository.saveAndFlush(meter.withClient(client));
        return client.withMeter(assigned);
    }

    public void unassignClientFrom(Meter meter) {
        meter.setClient(null);
        metersRepository.saveAndFlush(meter);
    }
}
