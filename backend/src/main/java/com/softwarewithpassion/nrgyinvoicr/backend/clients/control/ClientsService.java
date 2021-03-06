package com.softwarewithpassion.nrgyinvoicr.backend.clients.control;

import com.softwarewithpassion.nrgyinvoicr.backend.clients.entity.Client;
import com.softwarewithpassion.nrgyinvoicr.backend.clients.entity.ClientsSearchCriteria;
import com.softwarewithpassion.nrgyinvoicr.backend.meters.control.MetersService;
import com.softwarewithpassion.nrgyinvoicr.backend.meters.entity.Meter;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.softwarewithpassion.nrgyinvoicr.backend.clients.entity.ClientsSpecification.*;

@Service
public class ClientsService {
    private final ClientsRepository clientsRepository;
    private final ClientNumberGenerator clientNumberGenerator;
    private final MetersService metersService;

    ClientsService(ClientsRepository clientsRepository, ClientNumberGenerator clientNumberGenerator, MetersService metersService) {
        this.clientsRepository = clientsRepository;
        this.clientNumberGenerator = clientNumberGenerator;
        this.metersService = metersService;
    }

    public Client createClient(Client client) throws MeterAlreadyAssignedToClientException {
        Client newClient = client
                .withId(null)
                .withNumber(clientNumberGenerator.generateClientNumber());
        Meter meter = findMeterFor(newClient);
        validateMeterToAssignTo(newClient, meter);
        Client savedClient = clientsRepository.save(newClient.withMeter(null));
        return metersService.toClientWithAssignedMeter(savedClient, meter);
    }

    public Optional<Client> getClient(Long id) {
        return clientsRepository.findById(id);
    }

    public List<Client> findAll() {
        return clientsRepository.findAll();
    }

    public Page<Client> findClients(ClientsSearchCriteria criteria) {
        Specification<Client> specification = numberLike(criteria.getNumber())
                .and(firstNameLike(criteria.getFirstName()))
                .and(lastNameLike(criteria.getLastName()))
                .and(addressLike(criteria.getAddress()))
                .and(postalCodeLike(criteria.getPostalCode()))
                .and(cityLike(criteria.getCity()));
        return clientsRepository.findAll(specification, criteria.getPageDefinition().asPageRequest());
    }

    public Optional<Client> updateClient(Client updatedClient) throws MeterAlreadyAssignedToClientException {
        Meter meter = findMeterFor(updatedClient);
        validateMeterToAssignTo(updatedClient, meter);
        return clientsRepository.findById(updatedClient.getId())
                .map(this::withMeterUnassignedFrom)
                .map(c -> updatedClient.withNumber(c.getNumber()))
                .map(c -> c.withMeter(null))
                .map(clientsRepository::saveAndFlush)
                .map(c -> metersService.toClientWithAssignedMeter(c, meter));
    }

    private Meter findMeterFor(Client client) {
        return metersService
                .getMeter(client.getMeter().getId())
                .orElseThrow(() -> new RuntimeException("Meter does not exist, id: " + client.getMeter().getId()));
    }

    private void validateMeterToAssignTo(Client client, Meter meter) throws MeterAlreadyAssignedToClientException {
        if (meter.getClient() != null && (client.getId() == null || !meter.getClient().getId().equals(client.getId()))) {
            throw new MeterAlreadyAssignedToClientException(meter.getSerialNumber(), meter.getClient().getNumber(), client.getNumber());
        }
    }

    private Client withMeterUnassignedFrom(Client client) {
        if (client.getMeter() != null) {
            metersService.unassignClientFrom(client.getMeter());
        }
        return client;
    }
}
