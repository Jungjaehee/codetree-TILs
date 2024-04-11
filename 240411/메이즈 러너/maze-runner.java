import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
	static class Person implements Comparable<Person>{
		int num;
		int r;
		int c;
		boolean isExit;
		static int exitR;
		static int exitC;
		@Override
		public int compareTo(Person o) {
			int distance1 = Math.abs(this.r-exitR) + Math.abs(this.c-exitC);
			int distance2 = Math.abs(o.r-exitR) + Math.abs(o.c-exitC);
			
			return distance1 == distance2? (this.r == o.r ? Integer.compare(this.c, o.c):Integer.compare(this.r, o.r)):
				Integer.compare(distance1, distance2);
		}
	}
	static PriorityQueue<Person> pq;
	static int sumDistance = 0;
	static int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
	static int N, M;
	static Person[] persons;
	static int[][] map;
	static int[][] temp;
	static int exit_num = 0;
	
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		pq = new PriorityQueue<>();
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		int K = Integer.parseInt(st.nextToken());
		
		map = new int[N+1][N+1];
		temp = new int[N+1][N+1];
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		persons = new Person[M+1];
		for (int i = 1; i <= M; i++) {
			persons[i] = new Person();
			st = new StringTokenizer(br.readLine());
			persons[i].r = Integer.parseInt(st.nextToken());
			persons[i].c = Integer.parseInt(st.nextToken());
			persons[i].num = i+10;
			map[persons[i].r][persons[i].c] |= (1 << persons[i].num);
		}
		
		st = new StringTokenizer(br.readLine());
		Person.exitR = Integer.parseInt(st.nextToken());
		Person.exitC = Integer.parseInt(st.nextToken());
		map[Person.exitR][Person.exitC] = -1;
		
		for (int i = 0; i < K; i++) {
			moveAll();
			if(exit_num == M) break;
			int[] rect = selectRectangle();
			rotate(rect[0], rect[1], rect[2]);
		}
		System.out.println(sumDistance);
		System.out.println(Person.exitR + " " + Person.exitC);
	}
	
	private static void rotate(int r, int c, int distance) {
		int dr = r + distance;
		int dc = c + distance;
		
		for (int i = r; i < dr ; i++) {
			for (int j = c; j < dc; j++) {
				int val = map[dr - j + c - 1][i - r + c];
				if(val > 0 && val < 10) temp[i][j] = val-1;
				else temp[i][j] = val;
			}
		}
		
		for (int i = r; i < dr ; i++) {
			for (int j = c; j < dc; j++) {
				map[i][j] = temp[i][j];
				if(map[i][j] == -1) {
					Person.exitC = j;
					Person.exitR = i;
				}
				if(map[i][j] >= 1024) {
					for (int k = 1; k <= M; k++) {
						if((map[i][j] & (1 << persons[k].num)) != 0) {
							persons[k].r = i;
							persons[k].c = j;
						}
					}
				}
			}
		}
		
	}

	private static int[] selectRectangle() {
		pq.clear();
		for (int i = 1; i <= M; i++) {
			if(persons[i].isExit) continue;
			pq.offer(persons[i]);
		}
		Person p = pq.poll();
		
		int[] rect = new int[3]; //r, c, distance
		
		if(p.r == Person.exitR) {
			rect[2] = Math.abs(p.c - Person.exitC)+1;
			rect[1] = Math.min(p.c, Person.exitC);
			if(p.r - rect[2] < 1) rect[0] = 1;
			else rect[0] = p.r-rect[2];
		}
		else if(p.c == Person.exitC) {
			rect[2] = Math.abs(p.r - Person.exitR)+1;
			rect[0] = Math.min(p.r, Person.exitR);
			if(p.c - rect[2] < 1) rect[1] = 1;
			else rect[1] = p.c-rect[2];
		}
		else {
			int rAbs = Math.abs(p.r - Person.exitR);
			int cAbs = Math.abs(p.c - Person.exitC);
			if(rAbs < cAbs) {
				rect[2] = cAbs+1;
				rect[1] = Math.min(p.c, Person.exitC);
				int minR = Math.min(p.r, Person.exitR);
				int diff = rect[2] - rAbs;
				if(minR - diff < 1) rect[0] = 1;
				else rect[0] = minR-diff;
				
			}
			else if(rAbs > cAbs) {
				rect[2] = rAbs+1;
				rect[0] = Math.min(p.r, Person.exitR);
				int minC = Math.min(p.c, Person.exitC);
				int diff = rect[2] - cAbs;
				if(minC - diff < 1) rect[1] = 1;
				else rect[1] = minC-diff;
				
			}
			else {
				rect[0] = Math.min(p.r, Person.exitR);
				rect[1] = Math.min(p.c, Person.exitC);
				rect[2] = rAbs+1;
			}
		}
		
		return rect;
	}

	private static void moveAll() {
		for (int i = 1; i <= M; i++) {
			if(persons[i].isExit) continue;
			if(move(persons[i]))
				sumDistance++;
		}
	}
	
	private static boolean move(Person p) {
		int dir = -1;
		int minDistance = Math.abs(p.r - Person.exitR) + Math.abs(p.c - Person.exitC);
		for (int d = 0; d < 4; d++) {
			int nr = p.r + deltas[d][0];
			int nc = p.c + deltas[d][1];
			
			if(nr < 1 || nr > N || nc < 1 || nc > N || map[nr][nc] > 0 && map[nr][nc] < 10) continue;
			
			int dis =  Math.abs(nr - Person.exitR) + Math.abs(nc - Person.exitC);
			if(minDistance > dis) {
				minDistance = dis;
				dir = d;
			}
		}
		
		if(dir == -1) return false;
		int nr = p.r + deltas[dir][0];
		int nc = p.c + deltas[dir][1];
		map[p.r][p.c] ^= 1 << p.num;
		if(nr == Person.exitR && nc == Person.exitC) {
			p.isExit = true;
			exit_num++;
		}
		else {
			map[nr][nc] |= 1 << p.num;
			p.r= nr;
			p.c = nc;
		}
		return true;
	}

}