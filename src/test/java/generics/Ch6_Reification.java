package generics;

import org.junit.Test;

import java.util.*;

/**
 * Created by ikhoon on 08/07/2017.
 */
public class Ch6_Reification {
    // 6장 Reification
    // reify? 무슨 뜻일까?
    // 타입 관련된 글을 보다 보면 이런 글을 자주 본다.

    // 이번장에서는 instance 확인, casting, exception, arrays 에 대해서 알아본다.
    // 역시나 유익하다.
    // 기존에 코딩하면서 안되었던것들에 대한 근원적인 배경과 이론에 대해서 알려줄것이다.


    // 6.1 Reifiable Types
    // number of array의 reified type은 Number[]
    // number of list의 reified type은 ArrayList이다, ArrayList<Number>가 아니다.

    // 결국은 type erasure랑 관계되어 있다.
    // 이걸 계속 공부하다 보면 아... 자바, 아니 JVM 꼭 써야 하나?
    // 이런 생각만 계속 들었다.
    // 꼭 이렇게 까지 했어야 했나?
    // 언젠가는 type erasure 없어지는 날이 오겠지.
    // 아니면 JVM를 떠나는 날이 오던가



    // reifiable type
    // 어떤 타입이 런타입에 온전히 그정보를 다 가져가면 reifiable type 이라 부른다.
    // reifiable type에는 어떤것들이 있나?

    // * primitive type - int
    // * 타입 파라메터가 없는 class나 interface - Number, String
    // * 타입 파라메터가 있지만 모든 타입 인자가 unbounded wildcard 있때
    //   - List<?>, ArrayList<?>, Map<?, ?>
    // * raw type - List, ArrayList, Map
    // * component 타입이 reifiable인 array
    //   - int[], Number[], List<?>[], List[], or int[][]


    // 그렇다면 어떤 타입은 reifiable하지 않는단 말인가?
    // * type variable T
    // * 실제 타입으로 이루어진 타입 파라메터
    //   - List<Number>, ArrayList<String>, or Map<String, Integer>
    // * 바운드된 타입 파라메터
    //   -  List<? extends Number> or Comparable<? super String>

    // QUIZ : 아래의 코드는 동작할까?
    // 참고로 List<? extends Object>는 List<?> 와 같은 의미이다.

    @Test
    public void reifiableType() {
        Object a = new ArrayList<Integer>();
//        if(a instanceof List<? extends Object>) {
//            System.out.println("List<? extends Object>");
//        } else if(a instanceof List<?>) {
//            System.out.println("List<?>");
//        }
    }






    // * 그리고 특이하게 List<? extends Number>는 reifiable 하지 않는다. List<?>와 같은 건데도



    // 6.2 Instance Tests and Casts
    // instance tests와 casts는 런타임에 조사되는 타입에 의존한다.
    // 고로 reification에 의존한다.


    // 아래 코드는 아무런 문제없이 잘 동작한다.
    public class MyInteger extends Number {
        private final int value;
        public MyInteger(int value) { this.value=value; }
        public int intValue() { return value; }

        public boolean equals(Object o) {
            if (o instanceof Integer) { // test하고
                return value == (Integer) o; // casting한다.
            } else return false;
        }

        @Override public long longValue() { return 0; }
        @Override public float floatValue() { return 0; }
        @Override public double doubleValue() { return 0; }

    }


    // 그러나 아래 코드를 보자
    // 컴파일이 안된다.
    // 또한 generic 타입으로 casting하는건 warning이 뜬다.
    public abstract class MyAbstractList<E> extends AbstractCollection<E> implements List<E> {
        public boolean equals(Object o) {
            if (o instanceof List) {
//            if (o instanceof List<E>) { // compile-time error
                Iterator<E> it1 = iterator();
                Iterator<E> it2 = ((List<E>)o).iterator(); // unchecked cast
                while (it1.hasNext() && it2.hasNext()) {
                  E e1 = it1.next();
                  E e2 = it2.next();
                  if (!(e1 == null ? e2 == null : e1.equals(e2)))
                      return false;
                }
                return !it1.hasNext() && !it2.hasNext();
            } else return false;
        }
    }

    // 위의 코드가 동작한다 해 여전히 문제가 있다
    // [1,2,3]을 가지고 있는 List<Integer>와 List<Object>가 있다고 해보자.
    // 두개의 타입은 다르지만 안에 들어 있는 값은 같다.
    // 위의 코드가 동작한다해도 이런경우를 처리할수 없을것이다.


    // 이 문제를 해결하기 위해서
    // nonreifiable 타입을 reifiable 타입으로 바꾸어 보자.
    // 잘동작한다.
    // 이 코드가 잘 동작하는 이유는 값을 비교할때 Object의 equals를 이용하기 때문에
    // List<E>안에 어떤 타입이 있는지 상관없이 값이 비교가능하다.
    public abstract class MyAbstractList2<E> extends AbstractCollection<E> implements List<E> {
        public boolean equals(Object o) {
            if (o instanceof List<?>) {
              Iterator<E> it1 = iterator();
              Iterator<?> it2 = ((List<?>)o).iterator();
              while (it1.hasNext() && it2.hasNext()) {
                  E e1 = it1.next();
                  Object e2 = it2.next();
                  if (!(e1 == null ? e2 == null : e1.equals(e2)))
                      return false;
              }
              return !it1.hasNext() && !it2.hasNext();
            } else return false;
        }
    }


    // # Nonreifiable casts
    // instance tests는 항상 에러이다. reifiable한 타입이 아니면
    // instance casts는 하지만 어떤 상황에는 유효하다. reifiable한 타입이 아니여도

    public static <T> List<T> asList(Collection<T> c) throws IllegalArgumentException{
        if (c instanceof List<?>) {
            // c가 List<?> 타입이면 c는 항상 List<T> 타입이다.
            // 그렇기 때문에 이코드는 에러나 워닝없이 성공이다.
            return (List<T>)c;
        } else throw new IllegalArgumentException("Argument not a list");
    }


    // # Unchecked casts
    // 아주 일부분의 경우에만 컴파일러는 어떤 타입이 nonreifiable 이어도 casting이 성공한다.
    // 나머지의 경우에는 nonreifiable 타입으로 casting하는 경우 unchecked warning 이 뜬다.
    // 그리고 nonreifiable 타입으로 tests 하는건 항상에러이다.

    // 이런 casting하는것에 대하여 error가 아닌 warning으로 주는것은
    // 컴파일러는 List<String>으로 들어오는지 알수 없지만
    // 프로그래머는 알수 있기때문에 허락해준다 한다.


    public static List<String> promote(List<Object> objs) {
        for (Object o : objs)
            if (!(o instanceof String))
                throw new ClassCastException();
//        return (List<String>)objs; // QUIZ : 이건 컴파일 될가 안될까?
        return (List<String>)(List<?>)objs; // unchecked cast
    }

    @Test
    public void testPromote() {
        List<Object> objs1 = Arrays.<Object>asList("one","two");
        List<Object> objs2 = Arrays.<Object>asList(1,"two");
        List<String> strs1 = promote(objs1);
        assert (List<?>)strs1 == (List<?>)objs1;
        boolean caught = false;
        try {
            List<String> strs2 = promote(objs2);
        } catch (ClassCastException e) { caught = true; }
        assert caught;
    }


    // 우리는 코드에 unchecked casts의 숫자를 최소화 해야한다.
    // 하지만 어떤 경우에는 unchecked cast를 피할수 없다.
    // 그리고 의도적인 unchecked cast에 대해서는 같은 라인에 comment로 남겨주는것이 중요하다.
    // 왜냐믄 컴파일러가 warning을 찍을때 같은 라인의 코멘트도 보여주기 때문이다.
    /**
     % javac -Xlint:unchecked Promote.java
     Promote.java:7: warning: [unchecked] unchecked cast
     found : java.util.List
     required: java.util.List<java.lang.String>
        return (List<String>)(List<?>)objs; // unchecked cast
     1 warning
     */


    //그리고 진짜 필요한 warning이 아니면
    // @SuppressWarnings("unchecked") 넣으면 된다
    @SuppressWarnings("unchecked")
    public static List<String> promote2(List<Object> objs) {
        for (Object o : objs)
            if (!(o instanceof String))
                throw new ClassCastException();
        return (List<String>)(List<?>)objs; // unchecked cast
    }


    // 6.3 Exception Handling
    // 에러를 핸들링할때
    // catch 문에서 에러의 타입을 검증한다.
    // 이것은 instance tests에 의해서 수행되어 지는것이다.
    // 고로 그 타입은 reifiable 이어야 한다.
    // 몰랐쥬?

//    class ParametricException<T> extends Exception { // compile-time error
//        private final T value;
//        public ParametricException(T value) { this.value = value; }
//        public T getValue() { return value; }
//    }

    // 그래서 아예 원천적으로 exceptin class를 만들때 generic타입으로 못만들게 막아버렸다.



    // 6.4 Array Creation

    @Test
    public void arrayStoreException() {
        Integer[] ints = new Integer[] {1,2,3};
        // 이게 되는 이유는 Array 타입은 covariant이기 때문이다.
        Number[] nums = ints;
        // 이게 exception이 발생하는 이유는 이유는 reified 타입이랑 호환되지 않기 때문이다.
        nums[2] = 3.14; // array store exception
    }

    // array는 generic 생성이 안된다.
    // 이 코드는 동작하지 않는다.
    // 이유는 type variable은 reifiable type이 아니기 때문이다.
    public <T> void annoying() {
//        T[] a = new T[10]; // compile-time error
    }





    // 이코드는 역시 동작하지 않는다, 이유는 parameterized type은 reifiable type이 아니기 때문이다.
//    public List<Integer>[] alsoAnnoying() {
//        List<Integer> a = Arrays.asList(1,2,3);
//        List<Integer> b = Arrays.asList(4,5,6);
//        return new List<Integer>[] {a, b}; // compile-time error
//    }


    // 자바에서 generic array를 생성하지 못하는것은 가장 심각한 제약중에 하나이다.
    // 최선의 방법은 array를 사용하지 말고 ArrayList를 써라.

    // TODO 6.5 ~ 6.8



}
