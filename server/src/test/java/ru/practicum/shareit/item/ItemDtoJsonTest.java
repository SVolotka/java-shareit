package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(99L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Powerful drill");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(99);
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = """
                {
                    "name": "Hammer",
                    "description": "Heavy hammer",
                    "available": false,
                    "requestId": 42
                }
                """;

        ItemDto itemDto = json.parse(jsonContent).getObject();

        assertThat(itemDto.getName()).isEqualTo("Hammer");
        assertThat(itemDto.getDescription()).isEqualTo("Heavy hammer");
        assertThat(itemDto.getAvailable()).isFalse();
        assertThat(itemDto.getRequestId()).isEqualTo(42L);
    }
}