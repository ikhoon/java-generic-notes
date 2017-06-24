package generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CopyTest {
    // collection copy
    // CollectionA(1) => CollectionB(1)
    // b.set(1, a.get(1))

    // 이함수를 generic 하게 바꾸시오!
    public void copy1(List<Integer> from, List<Integer> to) {
        for (int i = 0; i < from.size(); i++) {
           to.add(i, from.get(i));
        }
    }
    // List<Integer> from, List<Integer> to
    // List<Integer> from, List<Object> to

    // 이함수를 generic 하게 바꾸시오!
    public <Int> void copy(List<? extends Int> from, List<? super Int> to) {
        for (int i = 0; i < from.size(); i++) {
            to.add(i, from.get(i));
        }
        // 치환의 법칙
        // 상위타입에는 하위타입을 대입할수 있다.
    }

    public void testCopy() {
        List<Integer> from = Arrays.asList(1, 2, 3);
        List<Number> to = Arrays.asList(4, 5, 6);
        this.<Integer>copy(from, to);

    }
}
