package org.jooq.cache.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

class IOUtils {

	// inputstream
	
	/**
	 * Read and close the InputStream
	 * @param in
	 * @return The bytes read in the InputStream
	 * @throws IOException
	 */
	static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			copyLarge(in, out);
		} finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch (Exception e2) {
			}
		}
		return out.toByteArray();
	}
	
    /**
	* Copy bytes from a large (over 2GB) <code>InputStream</code> to an
	* <code>OutputStream</code>.
	* <p>
	* This method buffers the input internally, so there is no need to use a
	* <code>BufferedInputStream</code>.
	*
	* @param input the <code>InputStream</code> to read from
	* @param output the <code>OutputStream</code> to write to
	* @return the number of bytes copied
	* @throws NullPointerException if the input or output is null
	* @throws IOException if an I/O error occurs
	* @since Commons IO 1.3
	*/
    static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    // reader
    
    /**
	 * Read and close the InputStream
	 * @param in
	 * @return The bytes read in the InputStream
	 * @throws IOException
	 */
	static byte[] toByteArray(Reader input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		OutputStreamWriter out = new OutputStreamWriter(output);
		try {
			copyLarge(input, out);
		} finally {
			try {
				if(out != null) {
					out.flush();
				}
			} catch (Exception e) {
			}
			try {
				if(input != null) {
					input.close();
				}
			} catch (Exception e2) {
			}
		}
		return output.toByteArray();
	}
	
    /**
     * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     *
     * @param input  the <code>Reader</code> to read from
     * @param output  the <code>Writer</code> to write to
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since Commons IO 1.3
     */
    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[1024 * 4];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
