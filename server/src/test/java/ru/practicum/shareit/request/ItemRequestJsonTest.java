package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestJsonTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need a drill");

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a drill");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = """
                {
                    "description": "Need a hammer"
                }
                """;

        ItemRequestDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getDescription()).isEqualTo("Need a hammer");
    }
}