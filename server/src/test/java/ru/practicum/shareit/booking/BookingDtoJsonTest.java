package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.of(2026, 1, 1, 0, 0));
        dto.setEnd(LocalDateTime.of(2026, 1, 2, 0, 0));

        JsonContent<BookingRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-01-01T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-01-02T00:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\n" +
                "    \"itemId\": 15,\n" +
                "    \"start\": \"2026-01-01T00:00:00\",\n" +
                "    \"end\": \"2026-01-02T00:00:00\"\n" +
                "}";

        BookingRequestDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getItemId()).isEqualTo(15L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 1, 1, 0, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 1, 2, 0, 0));
    }
}