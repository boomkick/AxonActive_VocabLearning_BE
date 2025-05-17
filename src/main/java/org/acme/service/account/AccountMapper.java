package org.acme.service.account;

import org.acme.service.account.dto.RequestRegisterAccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
    Account toAccount (RequestRegisterAccountDTO dto);
}
