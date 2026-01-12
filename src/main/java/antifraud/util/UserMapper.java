package antifraud.util;

import antifraud.model.user.UserEntity;
import antifraud.model.user.dto.UserDtoOut;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static List<UserDtoOut> userListToDtoList(List<UserEntity> list) {
        List<UserDtoOut> userDtoOuts = new ArrayList<>();
        for (UserEntity userEntity : list) {
            userDtoOuts.add(
                    new UserDtoOut(
                            userEntity.getId(),
                            userEntity.getName(),
                            userEntity.getUsername(),
                            userEntity.getRole().getRole().name()
                    ));
        }

        return userDtoOuts;
    }
}
