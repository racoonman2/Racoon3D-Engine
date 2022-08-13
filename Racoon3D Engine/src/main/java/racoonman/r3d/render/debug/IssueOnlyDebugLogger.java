package racoonman.r3d.render.debug;

import java.util.HashSet;
import java.util.Set;

public class IssueOnlyDebugLogger implements IDebugLogger {
	private Set<Integer> ignored;
	
	public IssueOnlyDebugLogger(int... ignored) {
		this.ignored = new HashSet<>(ignored.length);

		for(int ignore : ignored) {
			this.ignored.add(ignore);
		}
	}
	
	@Override
	public Severity[] getSeverities() {
		return new Severity[] { Severity.WARNING, Severity.ERROR };
	}

	@Override
	public Type[] getTypes() {
		return Type.values();
	}

	@Override
	public void log(Severity severity, Type type, String message, int id) {
		if(!this.ignored.contains(id)) {
			System.err.println(message);
		}
	}
}
