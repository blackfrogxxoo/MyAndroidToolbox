package org.wxc.myandroidtoolbox.utils;

import java.util.Arrays;

/**
 * 
 * @author zzc
 * 
 */
public final class TypeUtil {
	public static final String TAG = "TypeUtil";

	private TypeUtil() {
	}

	public static int parseInt(String source, int defaultValue) {
		int value = defaultValue;
		try {
			value = Integer.parseInt(source);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static float[] ints2Floats(int[] source) {
		if (source == null) {
			return null;
		}
		final float[] result = new float[source.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = source[i];
		}
		return result;
	}

	public static float[] concat(float[] first, float[] second) {
		if (first == null && second == null) {
			return new float[0];
		} else if (first == null) {
			return Arrays.copyOf(second, second.length);
		} else if (second == null) {
			return Arrays.copyOf(first, first.length);
		} else {
			float[] result = Arrays.copyOf(first, first.length + second.length);
			System.arraycopy(second, 0, result, first.length, second.length);
			return result;
		}
	}

    public static byte[] concat(byte[] first, byte[] second) {
        if (first == null && second == null) {
            return new byte[0];
        } else if (first == null) {
            return Arrays.copyOf(second, second.length);
        } else if (second == null) {
            return Arrays.copyOf(first, first.length);
        } else {
            byte[] result = Arrays.copyOf(first, first.length + second.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }
    }

    public static byte[] concatAll(byte[]... rest) {
        byte[] first = new byte[0];
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

	public static <T> T[] concat(T[] first, T[] second) {
		if (first == null && second == null) {
			return null;
		} else if (first == null) {
			return Arrays.copyOf(second, second.length);
		} else if (second == null) {
			return Arrays.copyOf(first, first.length);
		} else {
			T[] result = Arrays.copyOf(first, first.length + second.length);
			System.arraycopy(second, 0, result, first.length, second.length);
			return result;
		}
	}

	private static final int BYTE_COUNT_SHORT = 2;
	private static final int BYTE_COUNT_INT = 4;
	private static final int BYTE_COUNT_LONG = 8;

	public static short LEBytes2Short(byte[] array) {
		return (short) LEBytes2Long(array, BYTE_COUNT_SHORT);
	}

	public static int LEBytes2Int(byte[] array) {
		return (int) LEBytes2Long(array, BYTE_COUNT_INT);
	}

	public static long LEBytes2Long(byte[] array) {
		return LEBytes2Long(array, BYTE_COUNT_LONG);
	}

	// 0x12345678: 78 56 34 12 in memory from low address to high.
	private static long LEBytes2Long(byte[] array, int count) {
		if (array == null || array.length > count) {
			throw new IndexOutOfBoundsException(
					"array is null or array is too long");
		}
		final int size = array.length;
		byte[] data = new byte[count];
		System.arraycopy(array, 0, data, 0, size);
		Arrays.fill(data, size, count, getFill(array[size - 1]));
		long value = 0L;
		for (int i = 0; i < count; i++) {
			value |= (data[i] & 0xffL) << (8 * i);
		}
		return value;
	}

	private static byte getFill(byte highByte) {
		final byte fill;
		if ((highByte & 1 << 7) != 0) {
			fill = -1;
		} else {
			fill = 0;
		}
		return fill;
	}

	public static short BEBytes2Short(byte[] array) {
		return (short) BEBytes2Long(array, BYTE_COUNT_SHORT);
	}

	public static int BEBytes2Int(byte[] array) {
		return (int) BEBytes2Long(array, BYTE_COUNT_INT);
	}

	public static long BEBytes2Long(byte[] array) {
		return BEBytes2Long(array, BYTE_COUNT_LONG);
	}

	// 0x12345678: 12 34 56 78 in memory from low address to high.
	private static long BEBytes2Long(byte[] array, int count) {
		if (array == null || array.length > count) {
			throw new IndexOutOfBoundsException(
					"array is null or array is too long");
		}
		final int size = array.length;
		byte[] data = new byte[count];
		System.arraycopy(array, 0, data, count - size, size);
		Arrays.fill(data, 0, count - size, getFill(array[0]));
		long value = 0L;
		for (int i = 0; i < count; i++) {
			value |= (data[count - 1 - i] & 0xffL) << (8 * i);
		}
		return value;
	}

	public static byte[] short2LEBytes(short value) {
		return long2LEBytes(value, BYTE_COUNT_SHORT);
	}

	public static byte[] int2LEBytes(int value) {
		return long2LEBytes(value, BYTE_COUNT_INT);
	}

	public static byte[] long2LEBytes(long value) {
		return long2LEBytes(value, BYTE_COUNT_LONG);
	}

	private static byte[] long2LEBytes(long value, int size) {
		final int count = BYTE_COUNT_LONG;
		byte[] data = new byte[count];
		for (int i = 0; i < count; i++) {
			data[i] = (byte) ((value >> (8 * i)) & 0xffL);
		}
		byte[] result = new byte[size];
		System.arraycopy(data, 0, result, 0, size);
		return result;
	}

	public static byte[] short2BEBytes(short value) {
		return long2BEBytes(value, BYTE_COUNT_SHORT);
	}

	public static byte[] int2BEBytes(int value) {
		return long2BEBytes(value, BYTE_COUNT_INT);
	}

	public static byte[] long2BEBytes(long value) {
		return long2BEBytes(value, BYTE_COUNT_LONG);
	}

	private static byte[] long2BEBytes(long value, int size) {
		final int count = BYTE_COUNT_LONG;
		byte[] data = new byte[count];
		for (int i = 0; i < count; i++) {
			data[i] = (byte) ((value >> (8 * (count - 1 - i))) & 0xffL);
		}
		byte[] result = new byte[size];
		System.arraycopy(data, count - size, result, 0, size);
		return result;
	}

	private static final int BIT_COUNT_BYTE = 8;
	private static final int BIT_COUNT_SHORT = 16;
	private static final int BIT_COUNT_INT = 32;
	private static final int BIT_COUNT_LONG = 64;

	public static long subLong(long value, int start, int end) {
		return subLong(value, start, end, BIT_COUNT_LONG);
	}

	public static int subInt(int value, int start, int end) {
		return (int) subLong(value, start, end, BIT_COUNT_INT);
	}

	public static short subShort(short value, int start, int end) {
		return (short) subLong(value, start, end, BIT_COUNT_SHORT);
	}

	public static byte subByte(byte value, int start, int end) {
		return (byte) subLong(value, start, end, BIT_COUNT_BYTE);
	}

	private static long subLong(long value, int lowStart, int highEnd, int count) {
		if (lowStart < 0 || highEnd > count || lowStart > highEnd) {
			throw new IndexOutOfBoundsException("start or end invalid");
		}
		if (lowStart == 0 && highEnd == count) {
			return value;
		}
		long temp = 0L;
		for (int i = lowStart; i < highEnd; i++) {
			temp |= 1L << i; // very important here
		}
		final long result = (value & temp) >> lowStart;
		return result;
	}

	// private static long subLong(long value, int highStart, int lowEnd, int
	// count) {
	// if (highStart < 0 || lowEnd > count || highStart > lowEnd) {
	// throw new IndexOutOfBoundsException("start or end invalid");
	// }
	// if (highStart == 0 && lowEnd == count) {
	// return value;
	// }
	// long temp = 0L;
	// for (int i = highStart; i < lowEnd; i++) {
	// temp |= 1L << (count - 1 - i); // very important here
	// }
	// final int right = count - lowEnd;
	// final long result = (value & temp) >> right;
	// return result;
	// }


	public static String byteArray2HexString(byte[] bytes) {
		if (null == bytes) {
			return null;
		}
		// Log.i(TAG, "size: " + bytes.length);
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String str = Integer.toHexString(0xFF & b);
			while (str.length() < 2) {
				str = "0" + str;
			}
			hexString.append(str);
		}
		return hexString.toString();
	}

	public static String[] bytes2Hexs(byte[] bytes) {
		if (null == bytes) {
			return null;
		}
		String[] strings = new String[bytes.length];
		// Log.i(TAG, "size: " + bytes.length);
		StringBuilder hexString = new StringBuilder();
		int i=0;
		for (byte b : bytes) {
			String str = Integer.toHexString(0xFF & b);
			while (str.length() < 2) {
				str = "0" + str;
			}
			strings[i++] = "0x" + str;
		}
		return strings;
	}

}
