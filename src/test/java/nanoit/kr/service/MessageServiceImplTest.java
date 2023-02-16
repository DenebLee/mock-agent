package nanoit.kr.service;

import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Testcontainers
class MessageServiceImplTest {



    @Test
    void isAlive_ReturnsTrue_WhenCommonPingSucceeds() {
        // given
        MessageRepository mockRepository = mock(MessageRepository.class);
        when(mockRepository.commonPing()).thenReturn(true);
        MessageService service = new MessageServiceImpl(mockRepository);
        // when
        boolean result = service.isAlive();

        // then
        assertTrue(result);
    }

    @Test
    void isAlive_ReturnsFalse_WhenCommonPingFails() {
        // given
        MessageRepository mockRepository = mock(MessageRepository.class);
        when(mockRepository.commonPing()).thenThrow(new SelectFailedException("failed"));
        MessageService service = new MessageServiceImpl(mockRepository);

        // when
        boolean result = service.isAlive();

        // then
        assertFalse(result);
        verify(mockRepository).commonPing();
        verify(mockRepository).createTable();
    }


    @Test
    void count_ReturnsCount_WhenCommonCountSucceeds() {
        // given
        MessageRepository mockRepository = mock(MessageRepository.class);
        when(mockRepository.commonCount()).thenReturn(10L);
        MessageService service = new MessageServiceImpl(mockRepository);

        // when
        long result = service.count();

        // then
        assertEquals(10L, result);
        verify(mockRepository).commonCount();
    }

    @Test
    void count_ReturnsZero_WhenCommonCountFails() {
        // given
        MessageRepository mockRepository = mock(MessageRepository.class);
        when(mockRepository.commonCount()).thenThrow(new SelectFailedException("failed"));
        MessageService service = new MessageServiceImpl(mockRepository);

        // when
        long result = service.count();

        // then
        assertEquals(0L, result);
        verify(mockRepository).commonCount();
    }


//    @Test
//    void selectAll_ReturnsSendList_WhenSelectAndSelectedUpdateSucceed() throws InterruptedException {
//        // given
//        MessageRepository mockRepository = mock(MessageRepository.class);
//        List<MessageEntity> mockMessageEntities = new ArrayList<>();
//        mockMessageEntities.add(new MessageEntity(1L, "sender", "callback", "phone", "content"));
//        when(mockRepository.sendSelectAll()).thenReturn(mockMessageEntities);
//        when(mockRepository.selectedUpdate(mockMessageEntities)).thenReturn(true);
//        MessageService service = new MessageServiceImpl(mockRepository);
//
//        // when
//        List<Send> result = service.selectAll();
//
//        // then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(1L, result.get(0).getId());
//        assertEquals("sender", result.get(0).getSenderName());
//        assertEquals("callback", result.get(0).getCallbackNumber());
//        assertEquals("phone", result.get(0).getPhoneNumber());
//        assertEquals("content", result.get(0).getContent());
//        verify(mockRepository).sendSelectAll();
//        verify(mockRepository).selectedUpdate(mockMessageEntities);
//    }
}