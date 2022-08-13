package racoonman.r3d.resource.codec;

public interface IFunction<R> {
	R apply();
	
	static <R> R identity(R r) {
		return r;
	}

	static interface IFunction1<R, P1> {
		R apply(P1 first);
	}
	
	static interface IFunction2<R, P1, P2> {
		R apply(P1 first, P2 second);
	}
	
	static interface IFunction3<R, P1, P2, P3> {
		R apply(P1 first, P2 second, P3 third);
	}
	
	static interface IFunction4<R, P1, P2, P3, P4> {
		R apply(P1 first, P2 second, P3 third, P4 fourth);
	}
	
	static interface IFunction5<R, P1, P2, P3, P4, P5> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth);
	}
	
	static interface IFunction6<R, P1, P2, P3, P4, P5, P6> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth);
	}
	
	static interface IFunction7<R, P1, P2, P3, P4, P5, P6, P7> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth, P7 seventh);
	}
	
	static interface IFunction8<R, P1, P2, P3, P4, P5, P6, P7, P8> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth, P7 seventh, P8 eight);
	}
	
	static interface IFunction9<R, P1, P2, P3, P4, P5, P6, P7, P8, P9> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth, P7 seventh, P8 eight, P9 ninth);
	}
	
	static interface IFunction10<R, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth, P7 seventh, P8 eight, P9 ninth, P10 tenth);
	}
	
	static interface IFunction11<R, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth, P7 seventh, P8 eight, P9 ninth, P10 tenth, P11 eleventh);
	}

	static interface IFunction12<R, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth, P7 seventh, P8 eight, P9 ninth, P10 tenth, P11 eleventh, P12 twelfth);
	}
	
	static interface IFunction13<R, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth, P7 seventh, P8 eight, P9 ninth, P10 tenth, P11 eleventh, P12 twelfth, P13 thirteenth);
	}
	
	static interface IFunction14<R, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth, P7 seventh, P8 eight, P9 ninth, P10 tenth, P11 eleventh, P12 twelfth, P13 thirteenth, P14 fourteenth);
	}
	
	static interface IFunction15<R, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> {
		R apply(P1 first, P2 second, P3 third, P4 fourth, P5 fifth, P6 sixth, P7 seventh, P8 eight, P9 ninth, P10 tenth, P11 eleventh, P12 twelfth, P13 thirteenth, P14 fourteenth, P15 fifteenth);
	}
}
