package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;

/**
 * Deserializing an object. Custom deserializers should extend from
 * AbstractDeserializer to avoid issues with signature changes.
 */
public interface Deserializer {
	public Class<?> getType();

	public boolean isReadResolve();

	public Object readObject(AbstractInput in) throws IOException;

	public Object readList(AbstractInput in, int length)
			throws IOException;

	public Object readLengthList(AbstractInput in, int length)
			throws IOException;

	public Object readMap(AbstractInput in) throws IOException;

	/**
	 * Creates an empty array for the deserializers field entries.
	 * 
	 * @param len
	 *            number of fields to be read
	 * @return empty array of the proper field type.
	 */
	public Object[] createFields(int len);

	/**
	 * Returns the deserializer's field reader for the given name.
	 * 
	 * @param name
	 *            the field name
	 * @return the deserializer's internal field reader
	 */
	public Object createField(String name);

	/**
	 * Reads the object from the input stream, given the field definition.
	 * 
	 * @param in
	 *            the input stream
	 * @param fields
	 *            the deserializer's own field marshal
	 * @return the new object
	 * @throws IOException
	 */
	public Object readObject(AbstractInput in, Object[] fields)
			throws IOException;

	public Object readObject(AbstractInput in, String[] fieldNames)
			throws IOException;
}
