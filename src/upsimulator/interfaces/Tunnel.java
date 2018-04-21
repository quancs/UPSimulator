package upsimulator.interfaces;

import java.util.List;
import java.util.Map;

public interface Tunnel extends Cloneable {

	public enum TunnelType {
		Out, Here, In, // one target
		In_all, // no target
		In_one_of_all, // no target
		In_one_of_specified, // more than one targets connected with "|"
		In_all_of_specified, // more than one targets connected with "&"
		Go, // one target
		Go_all, // no target
		Go_one_of_all, // no target
		Go_all_of_specified, // more than one targets connected with "&"
		Go_one_of_specified// more than one targets connected with "|"
	};// son->father, self->self, a->(b,c,d)

	public void setType(TunnelType type);

	public TunnelType getType();

	public String getName();

	/**
	 * Target's names are combined by "|" or "&", according to the tunnel type, and
	 * all the names are sorted by {@code String.compareTo}
	 * 
	 * @return the united name of all the targets
	 */
	public String getTargetsName();

	public Membrane getSource();

	public void setSource(Membrane source);

	public List<Membrane> getTargets();

	public void addTarget(Membrane target);

	public void holdResult(Result result, int times);

	public Map<Result, Integer> getHeldResults();

	public void pushResult();

	public void open();

	public void close();

	public boolean isOpen();
}
