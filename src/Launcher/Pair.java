package Launcher;

public class Pair <T extends Comparable<T>,S> implements Comparable<Pair<T,S>>{
	
		T first;
		S second;
		public Pair(T ff, S ss){
			first=ff;
			second=ss;
		}
		@Override
		public int compareTo(Pair<T, S> o) {
			return first.compareTo(o.first);
		}
		public T getFirst() {
			return first;
		}
		public S getSecond() {
			return second;
		}

}
