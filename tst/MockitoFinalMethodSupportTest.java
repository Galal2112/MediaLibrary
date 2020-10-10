import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
    kann gelöscht werden wenn der Test funktioniert
 */
public class MockitoFinalMethodSupportTest {
    class ObjectWithFinalMethod {
        public final int test(){return 0;}
    }
    /*
    testet ob Mockito mit final zurechtkommt, ist nützlich für später
     */
    @Test
    void finalMethodSupportTest() {
        ObjectWithFinalMethod fo=mock(ObjectWithFinalMethod.class);
        when(fo.test()).thenReturn(4);
        assertEquals(4,fo.test());
    }
}
