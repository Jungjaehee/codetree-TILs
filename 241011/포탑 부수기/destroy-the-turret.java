import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	static class Elem implements Comparable<Elem> {
		int r;
		int c;
		int lastTurn;
		int potential;
		
		public Elem(int r, int c, int lastTurn, int potential) {
			super();
			this.r = r;
			this.c = c;
			this.lastTurn = lastTurn;
			this.potential = potential;
		}

		@Override
		public int compareTo(Elem o) {
			if(this.potential == o.potential) {
				if(this.lastTurn == o.lastTurn) {
					if((this.r + this.c)==(o.r + o.c)) {
						return -Integer.compare(this.c, o.c);
					}
					return -Integer.compare(this.r+this.c, o.r+o.c);
				}
				return -Integer.compare(this.lastTurn, o.lastTurn);
			}
			return Integer.compare(this.potential, o.potential);
		}
		
	}
	static class Root{
		int r;
		int c;
		List<int[]> path;
		
		public Root(int r, int c, List<int[]> path) {
			super();
			this.r = r;
			this.c = c;
			this.path = path;
		}
		
	}
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));;
		StringTokenizer st = new StringTokenizer(br.readLine());
		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());
		int K = Integer.parseInt(st.nextToken());
		
		int sum = N + M;
		int[][] map = new int[N][M];
		int[][] turn = new int[N][M];
		List<Elem> topList = new ArrayList<Elem>(N*M);
		
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
				
				if(map[i][j] != 0) {
					topList.add(new Elem(i, j, 0, map[i][j]));
				}
				else {
					map[i][j] = -1;
				}
			}
		}
		
		Queue<Root> queue = new ArrayDeque<>();
		int[][] deltas4 = {{0, 1},{1, 0},{0, -1}, {-1,0}};
		int[][] deltas8 = {{0, 1},{1, 0},{0, -1}, {-1,0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
		for (int i = 1; i <= K; i++) {
			Collections.sort(topList);
			Elem attacker = topList.get(0);
			
			map[attacker.r][attacker.c] += sum;
			turn[attacker.r][attacker.c] = i;
			
			Elem target = topList.get(topList.size()-1);
			
			boolean[][] isVisited = new boolean[N][M];
			Root start = new Root(attacker.r, attacker.c, new ArrayList<>());
			queue.offer(start);
			isVisited[attacker.r][attacker.c] = true;
			boolean isFind = false;
			
			while(!queue.isEmpty()) {
				Root cur = queue.poll();
				
				for(int d=0;d<4;d++) {
					int nr = cur.r + deltas4[d][0];
					int nc = cur.c + deltas4[d][1];
					
					if(nr < 0) nr = nr + N;
					if(nr > N-1) nr = 0;
					if(nc < 0) nc = nc + M;
					if(nc > M-1) nc = 0;
					
					if(isVisited[nr][nc] || map[nr][nc] == -1) continue;
					
					if(nr == target.r && nc == target.c) {
						isFind = true;
						map[nr][nc] -= (map[attacker.r][attacker.c] + 1);
						if(map[nr][nc] < 0)
							map[nr][nc] = -1;
						
						int att = map[attacker.r][attacker.c]/2 + 1;
						for (int[] nx : cur.path) {
							map[nx[0]][nx[1]] -= att;
							if(map[nx[0]][nx[1]] < 0)
								map[nx[0]][nx[1]] = -1;
						}
						
						queue.clear();
						map[attacker.r][attacker.c]--;
						break;
					}
					
					isVisited[nr][nc] = true;
					List<int[]> newPath = new ArrayList<int[]>();
					newPath.addAll(cur.path);
					newPath.add(new int[] {nr, nc});
					queue.offer(new Root(nr, nc, newPath));
					
				}
			}
			
			if(!isFind) {
				
				map[target.r][target.c] -= (map[attacker.r][attacker.c] + 1);
				if(map[target.r][target.c] < 0)
					map[target.r][target.c] = -1;
				
				int att = map[attacker.r][attacker.c]/2 + 1;
				for (int d = 0; d < 8; d++) {
					int nr = target.r + deltas8[d][0];
					int nc = target.c + deltas8[d][1];
					
					if(nr < 0) nr = nr + N;
					if(nr > N-1) nr = 0;
					if(nc < 0) nc = nc + M;
					if(nc > M-1) nc = 0;
					
					map[nr][nc] -= att;
					if(map[nr][nc] < 0)
						map[nr][nc] = -1;
				}
				
				map[attacker.r][attacker.c]--;
			}
			
			topList.clear();
			for (int j = 0; j < N; j++) {
				for (int k = 0; k < M; k++) {
					if(map[j][k] == -1) continue;
					map[j][k]++;
					topList.add(new Elem(j, k, turn[j][k], map[j][k]));
				}
			}
		}
		
		Collections.sort(topList);
		System.out.println(topList.get(topList.size()-1).potential);
	}
}