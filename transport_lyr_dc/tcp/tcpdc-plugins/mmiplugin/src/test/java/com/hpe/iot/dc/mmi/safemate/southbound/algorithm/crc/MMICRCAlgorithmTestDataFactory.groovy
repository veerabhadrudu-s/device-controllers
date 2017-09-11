package com.hpe.iot.dc.mmi.safemate.southbound.algorithm.crc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sveera
 *
 */
public class MMICRCAlgorithmTestDataFactory {

	private final List<MMICRCAlgorithmTestData> dataSets;

	public MMICRCAlgorithmTestDataFactory() {
		super();
		this.dataSets = new ArrayList<>();
		// 64 64 102 0 1 51 48 49 48 55 49 53 48 48 48 48 55 0 0 0 0 0 0 0 0 64
		// 1 29 11 16 14 51 31 52 36 -55 2 -4 42 -83 16 0 0 0 0 3 0 0 0 0 100 0
		// 0 0 0 0 0 0 -5 97 15 -95 80 55 49 56 95 83 32 67 69 32 73 78 70 79 32
		// 86 49 46 48 46 49 0 80 55 49 56 95 72 32 86 49 46 48 46 51 0 61 19 13
		// 10
		dataSets.add(new MMICRCAlgorithmTestData(
				[ 64, 64, 102, 0, 1, 51, 48, 49, 48, 55, 49, 53, 48, 48, 48, 48, 55, 0, 0, 0, 0, 0, 0, 0, 0,
						64, 1, 29, 11, 16, 14, 51, 31, 52, 36, -55, 2, -4, 42, -83, 16, 0, 0, 0, 0, 3, 0, 0, 0, 0, 100,
						0, 0, 0, 0, 0, 0, 0, -5, 97, 15, -95, 80, 55, 49, 56, 95, 83, 32, 67, 69, 32, 73, 78, 70, 79,
						32, 86, 49, 46, 48, 46, 49, 0, 80, 55, 49, 56, 95, 72, 32, 86, 49, 46, 48, 46, 51, 0 ] as byte[],
				15635, "3d13"));

		// 64 64 102 0 1 51 48 49 48 55 49 53 48 48 48 48 55 0 0 0 0 0 0 0 0 64
		// 1 1 12 16 9 54 48 52 36 -55 2 -4 42 -83 16 0 0 0 0 3 0 0 0 0 100 0 0
		// 0 0 0 0 0 -5 97 15 -95 80 55 49 56 95 83 32 67 69 32 73 78 70 79 32
		// 86 49 46 48 46 49 0 80 55 49 56 95 72 32 86 49 46 48 46 51 0 -120 -93
		// 13 10

		dataSets.add(new MMICRCAlgorithmTestData(
				[ 64, 64, 102, 0, 1, 51, 48, 49, 48, 55, 49, 53, 48, 48, 48, 48, 55, 0, 0, 0, 0, 0, 0, 0, 0,
						64, 1, 1, 12, 16, 9, 54, 48, 52, 36, -55, 2, -4, 42, -83, 16, 0, 0, 0, 0, 3, 0, 0, 0, 0, 100, 0,
						0, 0, 0, 0, 0, 0, -5, 97, 15, -95, 80, 55, 49, 56, 95, 83, 32, 67, 69, 32, 73, 78, 70, 79, 32,
						86, 49, 46, 48, 46, 49, 0, 80, 55, 49, 56, 95, 72, 32, 86, 49, 46, 48, 46, 51, 0 ] as byte[],
				34979, "88a3"));
		// 64 64 102 0 1 51 48 49 48 55 49 53 48 48 48 48 55 0 0 0 0 0 0 0 0 64
		// 1 1 12 16 9 54 58 52 36 -55 2 -4 42 -83 16 0 0 0 0 3 0 0 0 0 100 0 0
		// 0 0 0 0 0 -5 97 15 -95 80 55 49 56 95 83 32 67 69 32 73 78 70 79 32
		// 86 49 46 48 46 49 0 80 55 49 56 95 72 32 86 49 46 48 46 51 0 -71 -60
		// 13 10
		dataSets.add(new MMICRCAlgorithmTestData(
				[ 64, 64, 102, 0, 1, 51, 48, 49, 48, 55, 49, 53, 48, 48, 48, 48, 55, 0, 0, 0, 0, 0, 0, 0, 0,
						64, 1, 1, 12, 16, 9, 54, 58, 52, 36, -55, 2, -4, 42, -83, 16, 0, 0, 0, 0, 3, 0, 0, 0, 0, 100, 0,
						0, 0, 0, 0, 0, 0, -5, 97, 15, -95, 80, 55, 49, 56, 95, 83, 32, 67, 69, 32, 73, 78, 70, 79, 32,
						86, 49, 46, 48, 46, 49, 0, 80, 55, 49, 56, 95, 72, 32, 86, 49, 46, 48, 46, 51, 0 ] as byte[],
				47556, "b9c4"));
		// 64 64 102 0 1 51 48 49 48 55 49 53 48 48 48 48 55 0 0 0 0 0 0 0 0 64
		// 1 1 12 16 9 55 8 52 36 -55 2 -4 42 -83 16 0 0 0 0 3 0 0 0 0 100 0 0 0
		// 0 0 0 0 -5 97 15 -95 80 55 49 56 95 83 32 67 69 32 73 78 70 79 32 86
		// 49 46 48 46 49 0 80 55 49 56 95 72 32 86 49 46 48 46 51 0 -42 -68 13
		// 10
		dataSets.add(new MMICRCAlgorithmTestData(
				[ 64, 64, 102, 0, 1, 51, 48, 49, 48, 55, 49, 53, 48, 48, 48, 48, 55, 0, 0, 0, 0, 0, 0, 0, 0,
						64, 1, 1, 12, 16, 9, 55, 8, 52, 36, -55, 2, -4, 42, -83, 16, 0, 0, 0, 0, 3, 0, 0, 0, 0, 100, 0,
						0, 0, 0, 0, 0, 0, -5, 97, 15, -95, 80, 55, 49, 56, 95, 83, 32, 67, 69, 32, 73, 78, 70, 79, 32,
						86, 49, 46, 48, 46, 49, 0, 80, 55, 49, 56, 95, 72, 32, 86, 49, 46, 48, 46, 51, 0 ] as byte[],
				54972, "d6bc"));
		// 64 64 102 0 1 51 48 49 48 55 49 53 48 48 48 48 55 0 0 0 0 0 0 0 0 64
		// 1 1 12 16 9 55 18 52 36 -55 2 -4 42 -83 16 0 0 0 0 3 0 0 0 0 100 0 0
		// 0 0 0 0 0 -5 97 15 -95 80 55 49 56 95 83 32 67 69 32 73 78 70 79 32
		// 86 49 46 48 46 49 0 80 55 49 56 95 72 32 86 49 46 48 46 51 0 -59 -122
		// 13 10
		dataSets.add(new MMICRCAlgorithmTestData(
				[ 64, 64, 102, 0, 1, 51, 48, 49, 48, 55, 49, 53, 48, 48, 48, 48, 55, 0, 0, 0, 0, 0, 0, 0, 0,
						64, 1, 1, 12, 16, 9, 55, 18, 52, 36, -55, 2, -4, 42, -83, 16, 0, 0, 0, 0, 3, 0, 0, 0, 0, 100, 0,
						0, 0, 0, 0, 0, 0, -5, 97, 15, -95, 80, 55, 49, 56, 95, 83, 32, 67, 69, 32, 73, 78, 70, 79, 32,
						86, 49, 46, 48, 46, 49, 0, 80, 55, 49, 56, 95, 72, 32, 86, 49, 46, 48, 46, 51, 0 ] as byte[],
				50566, "c586"));
		// 64 64 102 0 1 51 48 49 48 55 49 53 48 48 48 48 55 0 0 0 0 0 0 0 0 64
		// 1 1 12 16 9 55 28 52 36 -55 2 -4 42 -83 16 0 0 0 0 3 0 0 0 0 100 0 0
		// 0 0 0 0 0 -5 97 15 -95 80 55 49 56 95 83 32 67 69 32 73 78 70 79 32
		// 86 49 46 48 46 49 0 80 55 49 56 95 72 32 86 49 46 48 46 51 0 -76 114
		// 13 10
		dataSets.add(new MMICRCAlgorithmTestData(
				[ 64, 64, 102, 0, 1, 51, 48, 49, 48, 55, 49, 53, 48, 48, 48, 48, 55, 0, 0, 0, 0, 0, 0, 0, 0,
						64, 1, 1, 12, 16, 9, 55, 28, 52, 36, -55, 2, -4, 42, -83, 16, 0, 0, 0, 0, 3, 0, 0, 0, 0, 100, 0,
						0, 0, 0, 0, 0, 0, -5, 97, 15, -95, 80, 55, 49, 56, 95, 83, 32, 67, 69, 32, 73, 78, 70, 79, 32,
						86, 49, 46, 48, 46, 49, 0, 80, 55, 49, 56, 95, 72, 32, 86, 49, 46, 48, 46, 51, 0 ] as byte[],
				46194, "b472"));
		// 64 64 102 0 1 51 48 49 48 55 49 53 48 48 48 48 55 0 0 0 0 0 0 0 0 64
		// 1 1 12 16 9 55 38 52 36 -55 2 -4 42 -83 16 0 0 0 0 3 0 0 0 0 100 0 0
		// 0 0 0 0 0 -5 97 15 -95 80 55 49 56 95 83 32 67 69 32 73 78 70 79 32
		// 86 49 46 48 46 49 0 80 55 49 56 95 72 32 86 49 46 48 46 51 0 -29 -14
		// 13 10
		dataSets.add(new MMICRCAlgorithmTestData(
				[ 64, 64, 102, 0, 1, 51, 48, 49, 48, 55, 49, 53, 48, 48, 48, 48, 55, 0, 0, 0, 0, 0, 0, 0, 0,
						64, 1, 1, 12, 16, 9, 55, 38, 52, 36, -55, 2, -4, 42, -83, 16, 0, 0, 0, 0, 3, 0, 0, 0, 0, 100, 0,
						0, 0, 0, 0, 0, 0, -5, 97, 15, -95, 80, 55, 49, 56, 95, 83, 32, 67, 69, 32, 73, 78, 70, 79, 32,
						86, 49, 46, 48, 46, 49, 0, 80, 55, 49, 56, 95, 72, 32, 86, 49, 46, 48, 46, 51, 0 ] as byte[],
				58354, "e3f2"));

	}

	public List<MMICRCAlgorithmTestData> getDataSets() {
		return dataSets;
	}

}
