package kr.co.jhta.app.delideli.client.account.mapper;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface ClientMapper {
    void insertClient(ClientAccount client);
    Optional<ClientAccount> selectClientById(String clientId);
    Optional<ClientAccount> selectClientByEmail(String email);
    Optional<ClientAccount> selectClientByEIDAndName(String clientEID, String clientName);
    Optional<ClientAccount> selectClientByIdAndEIDAndName(String clientId, String clientEID, String clientName);
    void updatePwClient(ClientAccount client);
    boolean checkAccessAccount(ClientAccount clientAccount);
}
