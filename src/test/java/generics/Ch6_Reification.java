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


    // 6.5 The Principle of Truth in Advertising
    // 지난 섹션에서 collection 에서 array로 변환하는건 일반적인 방법으로는 되지않는다는걸 배웠다.
    // 먼저 unchecked cast를 넣어서 수정하려 해보자.
    // 앞에서 new T[c.size()] 하려다가 컴파일 오류가 발생했다.
    // 이번에는 꼼수로 Object[] 를 만든후에 casting T[] 하였다.
    // 이번에는 컴파일은 된다.
    // 하지만 실행해보면 동작하지 않는다.

    public <T> T[] toArray(Collection<T> c) {
        T[] a = (T[]) new Object[c.size()]; // unchecked cast int i=0; for (T x : c) a[i++] = x;
        return a;
    }
    @Test
    public void uncheckedCastCollectionToArray() {
        List<String> strings = Arrays.asList("one","two");
        // class cast error가 발생한다.
        String[] a = toArray(strings);
    }

    // 근데 에러의 위치가 예상과는 다르다. (unchecked cast위치에서 에러가 발생하는게 아니다.)
    // 정획히 좀더 자세히 코드를 뜯어보기 위해서
    // reified type 형태로 변형해보자.
    // reified 버전으로 보면 Object[]를 String[]로 변형되는 부분은 String[]에 값을 할당할때이다.
    // 첫번째 cast가 성공하더라도 2번째 cast가 실패이다.
    public Object[] toArrayWithReify(Collection c) {
        Object[] a = (Object[])new Object[c.size()]; // unchecked cast int i=0; for (Object x : c) a[i++] = x;
        return a;
    }

    @Test
    public void uncheckedCastCollectionToArrayWithReify() {
        List<String> strings = Arrays.asList("one", "two");
        String[] a = (String[]) toArray(strings); // class cast error }
    }
    // 이문제를 피하기 위해 우리는 아래의 원칙을 따라야 한다.
    // * The Principle of Truth in Advertising
    // - array의 reified는 타입은 그것의 static 타입의 erasure의 하위 타입이어야 한다.
    // 뭔말이래???? 어렵다. 찬찬히 알아보자.

    // 이원칙은 toArray의 body에 이원칙을 따라야 한다. main 함수가 아니다.

    // 그다음에 자바에서 cast-iron guarantee 를 한단다.
    // no cast inserted by erasure will fail, so long as there are no unchecked warnings
    // unchecked warning이 없는한 erasure로 삽인된 cast는 실패하지 않는다?

    // 반대로 이야기 하면
    // unchecked warning 있으면 erasure에서 삽입된 cast는 실패한다.

    // 소스 코드의 다른 부분에서 cast의 실패가 나는것은 unchecked warning에 책임이 있다.
    // 그렇기 때문에 unchecked warning에 대해서는 극도로 조심해야 한다고 한다.

    // Array Begets Array
    // generic array를 만드는 방법중 하나는 그 타입에 대해서 미리 가지고 있는거다.
    // 역시나 뭔말인가 싶다. 코드를 보니까 조금 이해가 된다.

    public <T> T[] toArrayGeneicWithAlready(Collection<T> c, T[] a) {
        if (a.length < c.size())
            // unchecked cast
            // reflection API를 이용해서 argument로 넘겨받은 generic array에서 타입 정보를 읽어서
            // 새로운 array를 만든다.
            a = (T[])java.lang.reflect.Array.
                    newInstance(a.getClass().getComponentType(), c.size());
        int i=0;
        for (T x : c) a[i++] = x;
        if (i < a.length) a[i] = null;
        return a;
    }

    // 아름답진 않지만 된다!
    // 그리고 또하나 눈여겨 봐야할거는
    /**
     * @see java.lang.reflect.Array#newInstance(Class, int)
     * 이함수이다. 여기서 이함수는 Object를 반환한다. Object[]가 아니라.
     * 그이유는 primitive 타입 int[]는 Object의 하위 타입이다. Object[]가 아니라.
     * 하지만 이런일은 실제 일어나지 않는다. generic T는 타입 변수는 reference type만 가능하기 때문이다.
     */
    @Test
    public void newArrayWithExistsType() {
        List<String> strings = Arrays.asList("one", "two");
        String[] a = toArrayGeneicWithAlready(strings, new String[0]);
        assert Arrays.toString(a).equals("[one, two]");
        String[] b = new String[]{"x", "x", "x", "x"};
        toArrayGeneicWithAlready(strings, b);
        assert Arrays.toString(b).equals("[one, two, null, x]");
    }

    /**
     *
     * @see ArrayList#toArray(Object[])
     * @see ArrayList#toArray()
     * 실제 Collection의 api도 우리가 방금 했던거와 비슷한 구현으로 되어 있다.
     * array의 class 정보를넘겨줘야지만 제대로된 array 타입을 반환한다.
     * 그렇지 않으면 Object[]를 반환한다.
     */
    @Test
    public void collectionFrameworkAPI() {
        List<String> strings = Arrays.asList("one", "two");
        Object[] objects = strings.toArray();
        String[] strings1 = strings.toArray(new String[0]);
    }


    // * A Classy Alternative
    // newInstance함수에서 class token을 얻기 위해서 T[]를 주는것 보다 Class<T>를 주는게 더 그럴듯해보인다.
    // Class<T> 에 대해서는 7장에서 더 자세히 배운다고 함
    public static <T> T[] toArrayWithClass(Collection<T> c, Class<T> k) {
        T[] a = (T[]) java.lang.reflect.Array.newInstance(k, c.size());
        int i = 0;
        for (T x : c) a[i++] = x;
        return a;
    }

    @Test
    public void newArrayWithClass() {
        List<String> strings = Arrays.asList("one", "two");
        String[] a = toArrayWithClass(strings, String.class);
        assert Arrays.toString(a).equals("[one, two]");
    }


    // 6.6 The Principle of Indecent Exposure 적절치 않은 노출의 원리
    // reifiable이 되지 않는 타입으로 array를 만드는것은 에러가 나지만
    // 특정 타입으로 array를 만들고 또 그 타입으로 unchecked cast하는건 가능하다.
    // 그러나 이 기능을 할때는 극도로 주의 해야한다.
    // 특히 nonreifiable 타입의 array를 public으로 노출하는건 절대 하지 말아야 한다.
    @Test
    public void arrayOfList() {
        // new List<Integer>[] ??
        List<Integer>[] intLists = (List<Integer>[]) new List[]{Arrays.asList(1)}; // unchecked cast
        List<? extends Number>[] numLists = intLists;
        numLists[0] = Arrays.asList(1.01);
        int n = intLists[0].get(0); // class cast exception!
    }

    // 이기능을 라이브러리로 만들때 더 주의 해야한다.
    // 이유는 warning은 library를 빌드할때 발생하지 사용하는곳에서는 warning도 보지 못하고 에러가 발생할수 있다.
    @Test
    public void collectionVsArray() {
        Integer[] ints = new Integer[]{1}; // unchecked cast
        Number[] nums = ints;
        nums[0] = 1.01;  // array store exception
        int n = ints[0];
    }
    // 위에 2개는 하나는 reifiable type 다른 하나는 nonreifiable type이다.
    // 에러가 발생하는곳이 다르다.
    // reifiable type은 저장시점에 발생하고
    // nonrefiable은 정장시에는 이슈가 없다가 그걸을 사용하려고 하면 오류가 발생한다/
    // 뭐든 에러가 늦게 발생하는건 좋지 않다. 잠재적인 에러를 유발할수 있다.

    // 그치만 둘다 안되긴 한다.

    // * Principle of Indecent Exposure
    // - array가 component가 reifiable 타입을 가지고 있지 않으면 public 하게 노출하지 말라.
    // - 그 이유는 unchecked cast가 완전히 다른 부분에서 class cast 에러를 발생하고 있기 때문이다.
    // - 그렇게 극도로 헷갈린단다.

    // 그러나 책의 예가 적절하지 않은건지 헷갈리긴 한다.
    // array of int나 list of int나 강제로 다른 타입으로 casting해서 저장하려고 하면
    // 저장하는 시점에서 에러가 난다.
    // 허나 array of list<int> 는 저장하는 시점에서 에러가 나지 않는다.
    // 사용하는 시점에서 에러가 난다.
    // 이게 문제인거 같다. 내가 잘못 저장하고 넘겼는데 사용하는 사람이 에러 크리를 맞는다.
    // 내가 잘못 저장할때 에러가 나야한다.
    @Test
    public void colllecionStore() {
       List<Integer> ints = Arrays.asList(1, 2, 3);
       List<?> nums = (List<?>) ints;
       List<Double> doubles = (List<Double>) nums;
       doubles.set(0, 1.1);
       double dou = doubles.get(0);
    }

    // java generic designer도 이 원칙 Principle of Indecent Exposure의 중요함을 이해하는데
    // 시간이 걸리다 보다라고 한다. 왜냐면 java reflection api가 generic을 반환하는데
    // array안에 nonreifiable type이 들어있다.
    // 겁내 깐다. 잘못했다고

    /**
     * TypeVariable<Class<T>>[] java.lang.Class.getTypeParameters()
     * TypeVariable<Method>[] java.lang.Reflect.Method.getTypeParameters()
     */


    // 6.7 How to Define ArrayList
    // 일반적으로 list가 array를 선호한다고 주장해왔다.
    // 특별한 이슈가 없으면 그렇게 하자.
    // 그런데 아주 일부의 경우(효율성이나 호환성) 때문에 array의 사용이 필요하다.
    // 또한 ArrayList를 구현할려면 array가 필요하다!!!
    // 이런거 구현할때 주의가 필요하다 하다.
    // Principle of Indecent Exposure, Principle of Truth in Advertising이 구현에 어떻게 쓰이는지 보자.

    // array의 new instance의 할당은 2군데서 이루어 진다.
    // 하나는 contructor, 또다른 하나는 add함수에서 호출하는 ensureCapacity에서 일어난다.
    // 역시나 Object[]로 만들고 E[]로 unchecked cast한다.
    // * 핵심은 array를 가지고 있는 필드는 private이다 그렇지 않으면 2개의 원칙 위반이다.
    // * E가 특정 타입(String)에 바운드 되었다면 Principle of Truth in Advertising에 위반이다.
    // -  Principle of Truth in Advertising - array의 reified는 타입은 그것의 static 타입의 erasure의 하위 타입이어야 한다.
    // 아 어렵다 포기할까?
    // * E가 특정타입(List<Integer>)의 바운드되어 있으면 Principle of Indecent Exposure 위반이란다.
    // 그이유는 reifiable 타입이 아니라서 그렇단다.
    // Principle of Anything Goes Behind Closed Doors

    // 이 라이브러리의 공동 저자는 이게 나쁜 스타일을 썼단다.
    // private Object[]로 저장하고 조회 할때 E로 casting 해야 한다 했데.
    // unchecked cast를 최소화 하기 위해서
    class MyArrayList<E> extends AbstractList<E> implements RandomAccess {
        private E[] arr;
        private int size = 0;

        public MyArrayList(int cap) {
            if (cap < 0) throw new IllegalArgumentException("Illegal Capacity: " + cap);
            arr = (E[]) new Object[cap]; // unchecked cast
        }
        public MyArrayList() { this(10); }
        public MyArrayList(Collection<? extends E> c) { this(c.size()); addAll(c); }

        public void ensureCapacity(int mincap) {
            int oldcap = arr.length;
            if (mincap > oldcap) {
                int newcap = Math.max(mincap, (oldcap * 3) / 2 + 1);
                E[] oldarr = arr;
                arr = (E[]) new Object[newcap]; // unchecked cast
                System.arraycopy(oldarr,0,arr,0,size);
            }
        }

        public int size() { return size; }

        private void checkBounds(int i, int size) {
            if (i < 0 || i >= size) throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
        }

        public E get(int i) {
            checkBounds(i, size);
            return arr[i];
        }

        public E set(int i, E elt) {
            checkBounds(i, size);
            E old = arr[i];
            arr[i] = elt;
            return old;
        }

        public void add(int i, E elt) {
            checkBounds(i, size + 1);
            ensureCapacity(size + 1);
            System.arraycopy(arr, i, arr, i + 1, size - i);
            arr[i] = elt;
            size++;
        }

        public E remove(int i) {
            checkBounds(i, size);
            E old = arr[i];
            arr[i] = null;
            size--;
            System.arraycopy(arr, i + 1, arr, i, size - i);
            return old;
        }
        // 앞에서 말했던 technique 을 쓰고 있다.
        public <T> T[] toArray(T[] a) {
            if (a.length < size)
                a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size); //
            System.arraycopy(arr, 0, a, 0, size);
            if (size < a.length) a[size] = null;
            return a;
        }

        public Object[] toArray() { return toArray(new Object[0]); }
    }


    // 휴. 좀 쉬자.


}
