package com.hclc.nrgyinvoicr.backend.clients.control;

import com.hclc.nrgyinvoicr.backend.clients.entity.ClientPlanAssignment;
import com.hclc.nrgyinvoicr.backend.clients.entity.ClientPlanAssignmentsSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientPlanAssignmentsService {
    private final ClientPlanAssignmentsRepository clientPlanAssignmentsRepository;

    public ClientPlanAssignmentsService(ClientPlanAssignmentsRepository clientPlanAssignmentsRepository) {
        this.clientPlanAssignmentsRepository = clientPlanAssignmentsRepository;
    }

    public ClientPlanAssignment createClientPlanAssignment(ClientPlanAssignment clientPlanAssignment) {
        clientPlanAssignment.setId(null);
        return clientPlanAssignmentsRepository.save(clientPlanAssignment);
    }

    public Page<ClientPlanAssignment> findClientPlanAssignments(long clientId, ClientPlanAssignmentsSearchCriteria criteria) {
        return clientPlanAssignmentsRepository.findByClientId(clientId, criteria.getPageDefinition().asPageRequest());
    }

    public Optional<ClientPlanAssignment> updateClientPlanAssignment(ClientPlanAssignment clientPlanAssignment) {
        return clientPlanAssignmentsRepository
                .findById(clientPlanAssignment.getId())
                .map(p -> clientPlanAssignmentsRepository.save(clientPlanAssignment));
    }
}