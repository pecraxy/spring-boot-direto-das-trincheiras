package academy.devdojo.profile;

import academy.devdojo.domain.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    Profile toProfile(ProfilePostRequest profilePostRequest);

    ProfilePostResponse toProfilePostResponse(Profile profile);

    ProfileGetResponse toProfileGetResponse(Profile profile);

    List<ProfileGetResponse> toProfileGetResponseList(List<Profile> profiles);
}
