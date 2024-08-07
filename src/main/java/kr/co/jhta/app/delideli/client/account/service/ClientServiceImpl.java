package kr.co.jhta.app.delideli.client.account.service;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.dto.ClientDTO;
import kr.co.jhta.app.delideli.client.account.mapper.ClientMapper;
import kr.co.jhta.app.delideli.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    @Autowired
    private final ClientMapper clientMapper;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override
    public void registerClient(ClientDTO clientDTO) {
        ClientAccount client = new ClientAccount();
        client.setClientEID(clientDTO.getClientEID());
        client.setClientName(clientDTO.getClientName());
        client.setClientPhone(clientDTO.getClientPhone());
        client.setClientId(clientDTO.getClientId());
        client.setClientPw(passwordEncoder.encode(clientDTO.getClientPw()));
        client.setClientEmail(clientDTO.getClientEmail());
        client.setBankName(clientDTO.getBankName());
        client.setBankAccount(clientDTO.getBankAccount());

        clientMapper.insertClient(client);
    }

    @Override
    public void updatePassword(String clientId, String newPassword) {
        ClientAccount clientAccount = findClientById(clientId);
        clientAccount.setClientPw(passwordEncoder.encode(newPassword));
        clientMapper.updatePwClient(clientAccount);
    }

    @Override
    public boolean checkAccessAccount(String clientId, String password) {
        ClientAccount clientAccount = findClientById(clientId);
        return clientMapper.checkAccessAccount(clientAccount);
    }

    @Override
    public boolean checkClientIdExists(String clientId) {
        return clientMapper.selectClientById(clientId).isPresent();
    }

    @Override
    public boolean checkClientEmailExists(String email) {
        return clientMapper.selectClientByEmail(email).isPresent();
    }

    @Override
    public ClientAccount findClientById(String clientId) {
        return clientMapper.selectClientById(clientId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with clientname: " + clientId));
    }

    @Override
    public boolean findIdAndSendEmail(String clientEID, String clientName) {
        Optional<ClientAccount> clientOpt = clientMapper.selectClientByEIDAndName(clientEID, clientName);
        if (clientOpt.isPresent()) {
            ClientAccount client = clientOpt.get();
            emailService.sendEmail(client.getClientEmail(), "delideli 사장 아이디", clientName + "님의 아이디는 " + client.getClientId() + " 입니다.");
            return true;
        }
        return false;
    }

    @Override
    public Optional<ClientAccount> validateClient(String clientId, String clientEID, String clientName) {
        return clientMapper.selectClientByIdAndEIDAndName(clientId, clientEID, clientName);
    }
}
