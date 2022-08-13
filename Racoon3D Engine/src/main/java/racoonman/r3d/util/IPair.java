package racoonman.r3d.util;

public interface IPair<L, R> {
	L left();
	
	R right();
	
	public static <L, R> IPair<L, R> of(L left, R right) {
		return new IPair<>() {

			@Override
			public L left() {
				return left;
			}

			@Override
			public R right() {
				return right;
			}			
		};
	}
	
	public static <L, R> IPair<L, R> empty() {
		return new IPair<>() {

			@Override
			public L left() {
				return null;
			}

			@Override
			public R right() {
				return null;
			}
		};
	}
}
