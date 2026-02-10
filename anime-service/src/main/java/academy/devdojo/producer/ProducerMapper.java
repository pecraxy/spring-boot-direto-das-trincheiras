package academy.devdojo.producer;

import academy.devdojo.domain.Producer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProducerMapper {
    Producer toProducer(ProducerPostRequest postRequest);

    Producer toProducer(ProducerPutRequest putRequest);

    ProducerGetResponse toProducerGetResponse(Producer producer);

    List<ProducerGetResponse> toProducerGetResponseList(List<Producer> producers);

    ProducerPostResponse toProducerPostResponse(Producer producer);
}
