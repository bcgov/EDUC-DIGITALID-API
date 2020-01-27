package ca.bc.gov.educ.api.digitalid.mappers;

import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UUIDMapper.class)
public interface DigitalIDEntityMapper {

    DigitalIDEntityMapper mapper = Mappers.getMapper(DigitalIDEntityMapper.class);

    DigitalID toStructure(DigitalIDEntity entity);

    DigitalIDEntity toModel(DigitalID struct);
}
