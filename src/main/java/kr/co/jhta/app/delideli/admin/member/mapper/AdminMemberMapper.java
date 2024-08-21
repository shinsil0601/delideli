package kr.co.jhta.app.delideli.admin.member.mapper;

import kr.co.jhta.app.delideli.admin.member.domain.AdminMemberAdmin;
import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMemberMapper {

    //사장님 회원 목록
    List<AdminMemberAdmin> getAllClientList();

    // 특정 회원의 정보 조회
    AdminMemberAdmin getClientByKey(int clientKey);

    // 승인 상태 업데이트
    void updateClientAccess(int clientKey, int clientAccess);

}
