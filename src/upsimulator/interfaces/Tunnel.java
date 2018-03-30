package upsimulator.interfaces;

import java.util.List;

public interface Tunnel extends Cloneable {

	public enum TunnelType {
		Out, Here, In, One2Many, One2ManyRandom
	};// son->father, self->self, a->(b,c,d)

	public void setType(TunnelType type);

	public TunnelType getType();

	public String getName();// son->parent, self->self, a->b

	/**
	 * 
	 * @return the united name of all the targets
	 */
	public String getTargetsName();

	public Membrane getSource();

	public void setSource(Membrane source);

	public List<Membrane> getTargets();

	public void addTarget(Membrane target);

	public void holdResult(Result result);

	public List<Result> getHeldResults();

	public void pushResult();

	public void open();

	public void close();

	public boolean isOpen();
}
