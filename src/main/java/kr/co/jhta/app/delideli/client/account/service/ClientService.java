package kr.co.jhta.app.delideli.client.account.service;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.dto.ClientDTO;

import java.util.Optional;

public interface ClientService {
    void registerClient(ClientDTO clientDTO);

    ClientAccount findClientById(String userId);

    boolean checkClientIdExists(String userId);

    boolean checkClientEmailExists(String email);


    boolean findIdAndSendEmail(String clientEID, String clientName);

    Optional<ClientAccount> validateClient(String clientId, String clientEID, String clientName);

    void updatePassword(String clientId, String newPassword);

    boolean checkAccessAccount(String clientId, String password);
}
