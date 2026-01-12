package antifraud.util;

import antifraud.model.ip.SuspiciousIpEntity;
import antifraud.model.ip.dto.SuspiciousIpDtoOut;

import java.util.ArrayList;
import java.util.List;

public class IpMapper {

    public static List<SuspiciousIpDtoOut> ipListToDtoList(List<SuspiciousIpEntity> list) {
        List<SuspiciousIpDtoOut> ipDtoOuts = new ArrayList<>();
        for (SuspiciousIpEntity suspiciousIpEntity : list) {
            ipDtoOuts.add(new SuspiciousIpDtoOut(suspiciousIpEntity.getId(), suspiciousIpEntity.getIp()));
        }
        return ipDtoOuts;
    }
}
