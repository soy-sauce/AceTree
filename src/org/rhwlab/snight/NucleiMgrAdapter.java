package org.rhwlab.snight;


import application_src.application_model.data.LineageData;
import application_src.application_model.data.TableLineageData;

import java.util.*;
import static java.lang.Integer.MIN_VALUE;

/*
 * Adapter to interface AceTree 3D Viewing with WormGUIDES
 *
 * Created: Oct. 2, 2015
 * Author: Braden Katzman
 */

public class NucleiMgrAdapter implements LineageData {

	public final List<Frame> timeFrames;
	private NucleiMgr nucleiMgr;
	private ArrayList<ArrayList<double[]>> allPositions;
	private Hashtable<String, int[]> cellOccurences;
	private int realTimePoints; /* NucleiMgr's ending index is past last time with cells present */
	private boolean isSulston;
	private double[] xyzScale;



	public NucleiMgrAdapter(NucleiMgr nucleiMgr) {
		this.nucleiMgr = nucleiMgr;
		//this.cellOccurences = new Hashtable<String, int[]>();
		this.realTimePoints = nucleiMgr.iEndingIndex; // initialize to this to avoid errors
		this.allPositions = new ArrayList<ArrayList<double[]>>();
		//preprocessCellOccurrences();
		//preprocessCellPositions();
		setIsSulstonModeFlag(nucleiMgr.iAncesTree.sulstonmode);
		System.out.println("NucleiMgrAdapter has isSulstonMode: " + isSulston);
		this.xyzScale = new double[3];
		this.xyzScale[0] = this.xyzScale[1] = nucleiMgr.iConfig.iXy_res;
		this.xyzScale[2] = nucleiMgr.iConfig.iZ_res;
		this.timeFrames = new ArrayList<>();

	}


	public class Frame {

		private List<String> names;
		private List<Double[]> positions;
		private List<Double> diameters;

		public Frame() {
			names = new ArrayList<>();
			positions = new ArrayList<>();
			diameters = new ArrayList<>();
		}

		public void shiftPositions(final double x, final double y, final double z) {
			for (int i = 0; i < positions.size(); i++) {
				final Double[] pos = positions.get(i);
				positions.set(i, new Double[]{pos[0] - x, pos[1] - y, pos[2] - z});
			}
		}

		public void addName(String name) {
			names.add(name);
		}

		public void addPosition(Double[] position) {
			positions.add(position);
		}

		public void addDiameter(Double diameter) {
			diameters.add(diameter);
		}

		public String[] getNames() {
			return names.toArray(new String[names.size()]);
		}

		public double[][] getPositions() {
			final double[][] copy = new double[positions.size()][3];
			for (int i = 0; i < positions.size(); i++) {
				for (int j = 0; j < 3; j++) {
					copy[i][j] = positions.get(i)[j];
				}
			}
			return copy;
		}

		public double[] getDiameters() {
			final double[] copy = new double[diameters.size()];
			for (int i = 0; i < diameters.size(); i++) {
				copy[i] = diameters.get(i);
			}
			return copy;
		}

		@Override
		public String toString() {
			String out = "";
			String[] names = getNames();
			for (String name : names) {
				out += name + "\n";
			}
			return out;
		}
	}
	//Scans through all timepoints and preprocesses all cells and their start and end occurrence
	/*private void preprocessCellOccurrences() {
		int timePoints = getNumberOfTimePoints();

		//start
		for (int i = 1; i <= timePoints; i++) {
			String[] names = getNames(i);

			if (names.length == 0) {
				this.realTimePoints = i;
				break;
			}

			for (int j = 0; j < names.length; j++) {
				String name = names[j];

				if (!cellOccurences.containsKey(name)) {
					int[] start_end = new int[2];
					start_end[0] = i;
					start_end[1] = i; // to avoid null exceptions if no end time point is found

					cellOccurences.put(name, start_end);
				}
			}
		}


		//end
		for (int i = realTimePoints; i > 0; i--) {
			String[] names = getNames(i);

			for (int j = 0; j < names.length; j++) {
				String name = names[j];

				if (cellOccurences.containsKey(name)) {
					cellOccurences.get(name)[1] = i;
				} else {
					System.out.println("no start occurence for: " + name);
				}
			}
		}

	}

	 */

	//preprocess cell positions for each time point
	/*private void preprocessCellPositions() {
		for (int i = 0; i < realTimePoints; i++) {
			ArrayList<double[]> positions_at_time = new ArrayList<double[]>();

			double[][] positions = getPositions(i, true);

			for (int j = 0; j < positions.length; j++) {
				double[] coords = positions[j];

				positions_at_time.add(coords);
			}

			allPositions.add(positions_at_time);
		}
	}
*/

	@Override
	public String[] getNames(int time) {
		if (time > 0) {
			Set<String> namesAL= new HashSet<>();


			//access vector of nuclei at given time frame
			Vector<Nucleus> v = nucleiMgr.nuclei_record.get(time);
//				Vector v = (Vector) nucleiMgr.nuclei_record.get(time - 1);

			//copy nuclei identities to ArrayList names AL
			for (int m = 0; m < v.size(); ++m) {
				Nucleus n = v.get(m);
				if (n.status == 1) {
					namesAL.add(n.identity); //push back identity
				}
			}


			List<String> list = new ArrayList<String>(namesAL);
			//convert ArrayList to String[]
			int size = namesAL.size();
			String[] names = new String[size];
			for (int i = 0; i < size; ++i) {
				names[i]=list.get(i);
			}

			return names;
		}
		return new String[0];



		//unedited
		/*if (time > 0) {
			ArrayList<String> namesAL = new ArrayList<>(); //named to distinguish between return String[] array

			//access vector of nuclei at given time frame
			Vector<Nucleus> v = nucleiMgr.nuclei_record.get(time);
//				Vector v = (Vector) nucleiMgr.nuclei_record.get(time - 1);

			//copy nuclei identities to ArrayList names AL
			for (int m = 0; m < v.size(); ++m) {
				Nucleus n = v.get(m);
				if (n.status == 1) {
					namesAL.add(n.identity); //push back identity
				}
			}

			//convert ArrayList to String[]
			int size = namesAL.size();
			String[] names = new String[size];
			for (int i = 0; i < size; ++i) {
				names[i] = namesAL.get(i);

			}

			return names;
		}
		return new String[0];*/
	}

	@Override
	public double[][] getPositions(int time) {
		if (allPositions == null) {
			//preprocessCellPositions();
		}

		ArrayList<double[]> positions = allPositions.get(time);

		double[][] positions_array = new double[positions.size()][3];

		for (int i = 0; i < positions.size(); i++) {
			positions_array[i] = positions.get(i);
		}

		return positions_array;
	}




	private double[][] getPositions(int time, boolean prvte) {
		ArrayList<ArrayList<Double>> positionsAL = new ArrayList<ArrayList<Double>>();

		//access vector of nuclei at given time frame
		Vector<Nucleus> v = nucleiMgr.nuclei_record.get(time);

		//copy nuclei positions to ArrayList positionsAL
		for (int m = 0; m < v.size(); ++m) {
			Nucleus n = (Nucleus) v.get(m);
			if (n.status == 1) {
				ArrayList<Double> position = new ArrayList<Double>(Arrays.asList((double)n.x, (double)n.y, (double)n.z));
				positionsAL.add(position);
			}
		}

		//convert ArrayList to Integer[][]
		int size = positionsAL.size(); //numbers of rows i.e. nuclei

		double[][] positions = new double[size][3]; //nuclei x 3 positions coordinates
		for (int i = 0; i < size; ++i) {
			ArrayList<Double> row = positionsAL.get(i);

			positions[i][0] = row.get(0);
			positions[i][1] = row.get(1);
			positions[i][2] = row.get(2);
		}

		return positions;

	}

	@Override
	public double[] getDiameters(int time) {
		ArrayList<Double> diametersAL = new ArrayList<Double>();

		//access vector of nuclei at given time frame
		Vector<Nucleus> v = nucleiMgr.nuclei_record.get(time);

		for (int m = 0; m < v.size(); ++m) {
			Nucleus n = (Nucleus) v.get(m);
			if (n.status == 1) {
				diametersAL.add((double)n.size);
			}
		}

		//convert ArrayList to Integer[]
		int size = diametersAL.size();
		double[] diameters = new double[size];
		for (int i = 0; i < size; ++i) {
			diameters[i] = diametersAL.get(i);
		}

		return diameters;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList<String> getAllCellNames() {
		ArrayList<String> allCellNames = new ArrayList<>();
		for (int i = 0; i < realTimePoints; i++) {
			String[] namesAti = getNames(i);
			for (String name : namesAti) {
				if (!allCellNames.contains(name)) {
					allCellNames.add(name);
				}
			}
		}

		return allCellNames;

		/*Set<String> allCellNames = new HashSet<>();
		for (int i = 0; i < realTimePoints; i++) {
			String[] namesAti = getNames(i);
			for (String name : namesAti) {
				allCellNames.add(name);
			}
		}

		ArrayList<String> result=new ArrayList<>(allCellNames);
		return result;*/
	}

	@Override
	public int getNumberOfTimePoints() {
		return this.realTimePoints;
	}

	/*
	 * If slow, optimize with preprocessing of cells and their first and last occurrences
	 *
	 * (non-Javadoc)
	 * @see wormguides.model.LineageData#getFirstOccurrenceOf(java.lang.String)
	 */
	@Override
	public int getFirstOccurrenceOf(String name) {
	/*	int[] start_end = cellOccurences.get(name);

		if (start_end != null) {
			return start_end[0];
		}

		return 0;*/


		System.out.println("get first occurence");
		int timePoints=getNumberOfTimePoints();
		for (int i = 1; i <= timePoints; i++) {
			String[] names = getNames(i);

			if (names.length == 0) {
				this.realTimePoints = i;
				break;
			}

			for (int j = 0; j < names.length; j++) {
				if(names[j].equalsIgnoreCase(name)){
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public int getLastOccurrenceOf(String name) {
		System.out.println("get last occurence");
		for (int i = realTimePoints; i > 0; i--) {
			String[] names = getNames(i);

			for (int j = 0; j < names.length; j++) {
				if(names[j].equalsIgnoreCase(name)){
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public boolean isCellName(String name) {
		return getAllCellNames().contains(name);
	}

	@Override
	public void shiftAllPositions(double x, double y, double z) {
		for (int i = 0; i < allPositions.size(); i++) {
			ArrayList<double[]> positions_at_frame = allPositions.get(i);

			for (int j = 0; j < positions_at_frame.size(); j++) {
				double[] coords = positions_at_frame.get(j);

				positions_at_frame.set(j, new double[] { coords[0] - x, coords[1] - y, coords[2] - z });
			}
			allPositions.set(i, positions_at_frame);
		}
	}

	@Override
	public boolean isSulstonMode() {
		return isSulston;
	}

	@Override
	public void setIsSulstonModeFlag(boolean isSulston) {
		this.isSulston = isSulston;
	}

	@Override
	public double[] getXYZScale() {
		return this.xyzScale;
	}





}
