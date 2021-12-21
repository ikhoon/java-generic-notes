package generics;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by ikhoon on 17/07/2017.
 */
public class Ch7_Reflection {
    // Reflection - 프로그램이 자신의 정의를 조사, 알아내는것.

    // class browsers, object inspectors, debuggers,
    // interpreters, services such as JavaBeansTM and object serialization
    // 이런것들을 한단다.

    // 7.1 Generics for Reflection
    // java의 reflection은 1.0부터 지원함
    // Class<T> 에 대해서 알아보자. 자주 보는 표현이지만 사실 우리는 잘 알지 못한다.
    // Class는 런타임의 객체의 정보를 전달한다.
    // 타입에다 .class 혹은 instance에다가 .getClass를 호출한다.

    // class token은 런타임에 전달되는 객체의 reified 타입 정보를 전달한다.
    @Test
    public void classToken() {
        Class ki = Integer.class;
        Number n = new Integer(42);
        Class kn = n.getClass();
        assert ki == kn;
    }


    // 자바 5 에서 Class<T> 를 제공한다.
    // QUIZ : 아래 코드는 컴파일이 될까?
    // Integer n = 10;
    // Class<Integer> k = n.getClass();







    // 역시나 우리는 자바를 모르는것 같다.
    // 반성합니다.
    // T.class 는 Class<T> 이다.
    // e가 T 타입의 변수라면 e.getClass() 는 Class<? extends T> 이다.
    @Test
    public void classTokenJava5() {
        Class<Integer> ki = Integer.class;
        Number n = new Integer(42);
        Class<? extends Number> kn = n.getClass();
        assert ki == kn;
    }

    // reflection을 쓰는 많은 경우에 class token의 정확한 타입이 필요하지 않다.
    // 그럴땐 wildcard를 쓰면 된다. Class<?>
    // 그러나 type parameter로 주는 정보가 가치가 없을때가 있다.
    // 그럴땐 Class<T>를 쓰자.


    // 7.2 Reflected Types are Reifiable Types
    // class는 항상 reifiable type을 표현한다.
    // 그래서 class의 타입 파라메터에 nonreifiable 타입은 들어갈수 없다.
    public void classTypeParameter() {
        List<Integer> ints = new ArrayList<Integer>();
        Class<? extends List> k = ints.getClass();

        ArrayList<Integer> ints2 = new ArrayList<Integer>();
        Class<? extends ArrayList> aClass = ints2.getClass();

        Class<ArrayList> arrayListClass = ArrayList.class;

//        Class<ArrayList<Int>> l = ints.getClass(); // 문법 오류이다.
        // 그런데 ArrayList<?>는 reifiable type이다.
        // 하지만  Class의 타입 파라메터로는 넣을수 없다. raw type을 넣어야 한단다.
    }


    public void genericClassLiteral() {
//       Class<?> k = List<Integer>.class; // 문법 오류이다.
        Class<?> k = List.class;
    }

    // 7.3 Reflection for Primitive Types
    // 자바의 모든 타입(primitive type, array 포함)하여 class literal과 그애 해당하는 class token을 가지고 있다.

    public void primitiveTypeClass() {
        Class<Integer> integerClass = int.class;
        Class<String[]> aClass = String[].class;
        // QUIZ int[] 의 class token은?

    }

    // int는 refenece type이 아니다. 그래서 token은 Class<Integer>이다.
    // 하지만 이 설계는 어리석은 결정이었다
    // class literal 이 동작하면 뭐하나 아래 코드는 동작하지도 않는데.
    @Test
    public void intCast() {
        Object o = new Integer(10);
        Integer cast = int.class.cast(o);
    }
    @Test
    public void intNewInstance() throws IllegalAccessException, InstantiationException {
        
        Integer integer = int.class.newInstance();
    }

    // 7.4 A Generic Reflection Library
    public static class Foo {
        public Foo() { }
    }
    @Test
    public void integerNewInstance() throws IllegalAccessException, InstantiationException {
        Integer i = 10;
        Integer integer = Integer.class.newInstance();
    }

    // new instance에 대한 문서는
    // https://docs.oracle.com/javase/tutorial/reflect/member/ctorInstance.html
    // 참조하는게 좋다.

    @Test
    public void fooNewInstance() throws IllegalAccessException, InstantiationException {
        Foo foo = Foo.class.newInstance();
    }

    public static class Email {
        private Set<String> aliases1;
        private Set<String> aliases2;
        private Email(HashMap<String, String> h) {
            aliases1 = h.keySet();
        }
        private Email(HashMap<String, String> h1, HashMap<String, String> h2) {
            aliases1 = h1.keySet();
            aliases2 = h2.keySet();
        }

        public void printKeys() {
            System.out.format("Mail keys:%n");
            for (String k : aliases1)
                System.out.format("  %s%n", k);
        }
    }

    @Test
    public void emailNewInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Email> declaredConstructor = Email.class.getDeclaredConstructor(HashMap.class, HashMap.class);
        declaredConstructor.setAccessible(true);
        Email email = declaredConstructor.newInstance(new HashMap<String, String>(), new HashMap<String, String>());
    }


    // Generic reflection
    public static class GenericReflection {
        // 하나의 객체를 받으면 같은 타입의 새로운 객체를 생성할수 있게 한다.
        // 이렇게 하는 이유는 T.class 같은게 되지 않기 때문에
        // instance로 부터 class 정보를 얻어서 새로운 객체는 생성하는것 같다.`
        public static <T> T newInstance(T obj) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            Object o = obj.getClass().getConstructor().newInstance();
            return (T) o; // unchecked cast
        }

        // T[].class 은 실행될수 없다.
        // 그렇기 때문에 generic T[] 타입의 class정보는 runtime의 instance의 token 정보로 부터 얻을수 있다.
        public static <T> Class<? extends T> getComponentType(T[] a) {
            Class<?> k = a.getClass().getComponentType();
            return (Class<? extends T>) k;
        }

        public static <T> T[] newArray(Class<? extends T> k, int size) {
            if(k.isPrimitive()) throw new IllegalArgumentException();
            Object o = java.lang.reflect.Array.newInstance(k, size);
            return (T[]) o;
        }

        public static <T> T[] newArray(T[] a, int size) {
            return newArray(getComponentType(a), size);
        }
    }

    // TODO


}
