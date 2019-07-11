package physica.api.core.cable;

public enum EnumConductorType {
	copper(120, 15360), silver(240, 61440), gold(360, 138240), superConductor(-1, 307200);

	public String getName()
	{
		return name();
	}

	private int	voltage;
	private int	transferRate;

	private EnumConductorType(int voltage, int transferRate) {
		this.voltage = voltage;
		this.transferRate = transferRate;
	}

	public int getVoltage()
	{
		return voltage;
	}

	public int getTransferRate()
	{
		return transferRate;
	}
}