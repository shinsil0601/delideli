package kr.co.jhta.app.delideli.admin.member.service;

import kr.co.jhta.app.delideli.admin.member.domain.AdminMemberAdmin;
import kr.co.jhta.app.delideli.admin.member.mapper.AdminMemberMapper;
import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AdminMemberServiceImpl implements AdminMemberService {
    @Autowired
    private final AdminMemberMapper adminMemberMapper;

    public AdminMemberServiceImpl(AdminMemberMapper adminMemberMapper) {
        this.adminMemberMapper = adminMemberMapper;
    }

    //사장님 회원 목록
    @Override
    public List<AdminMemberAdmin> getAllclientList() {
        List<AdminMemberAdmin> list = adminMemberMapper.getAllClientList();

        // 입력과 출력 포맷터 정의
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        for (AdminMemberAdmin admin : list) {
            if (admin.getClientRegdate() != null) {
                // String을 LocalDateTime으로 파싱한 후 원하는 형식으로 변환
                LocalDateTime dateTime = LocalDateTime.parse(admin.getClientRegdate(), inputFormatter);
                String formattedDate = dateTime.format(outputFormatter);
                admin.setClientRegdate(formattedDate);
            }
        }

        return list;
    }

    // 승인 상태를 토글하는 메서드
    @Override
    public int toggleClientAccess(int clientKey) {
        log.info("toggleClientAccess>>>> {}", clientKey);
        AdminMemberAdmin admin = adminMemberMapper.getClientByKey(clientKey);
        int newAccessStatus = admin.isClientAccess() ? 0 : 1;  // clientAccess 값에 따른 토글
        adminMemberMapper.updateClientAccess(clientKey, newAccessStatus);
        return newAccessStatus;
    }


}
