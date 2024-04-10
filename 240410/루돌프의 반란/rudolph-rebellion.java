import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main{
	static class Santa implements Comparable<Santa>{
		int num;
		int r;
		int c;
		boolean isDead = false;
		int jumsu;
		int kijul;
		static int rudolf_r;
		static int rudolf_c;
		
		@Override
		public int compareTo(Santa o) {
			int r1 = (this.r - rudolf_r);
			int c1 = (this.c - rudolf_c);
			int r2 = (o.r - rudolf_r);
			int c2 = (o.c - rudolf_c);
			int distance1 = r1*r1 + c1*c1;
			int distance2 = r2*r2 + c2*c2;
			return distance1 == distance2? 
					(this.r == o.r? 
							-Integer.compare(this.c, o.c) 
							: -Integer.compare(this.r, o.r))
					: Integer.compare(distance1, distance2);
		}

		@Override
		public String toString() {
			return "Santa [num=" + num + ", r=" + r + ", c=" + c + ", isDead=" + isDead + ", jumsu=" + jumsu
					+ ", kijul=" + kijul + "]\n";
		}
		
		
	}
	static int[][] deltas = {{-1, 0}, {0, 1}, {1, 0},{0, -1},
			{-1, -1}, {1, 1},{-1, 1},{1, -1}};
	
	static Santa[] santas;
	static PriorityQueue<Santa> pq;
	static Queue<Santa> queue;
	static int N, P;
	static int[][] map;
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		int C = Integer.parseInt(st.nextToken());
		int D = Integer.parseInt(st.nextToken());
		
		santas = new Santa[P+1];
		pq = new PriorityQueue<Santa>();
		queue = new ArrayDeque<>();
		map = new int[N+1][N+1];
		
		st = new StringTokenizer(br.readLine());
		Santa.rudolf_r = Integer.parseInt(st.nextToken());
		Santa.rudolf_c = Integer.parseInt(st.nextToken());
		map[Santa.rudolf_r][Santa.rudolf_c] = P+1;
		
		for (int i = 1; i <= P; i++) {
			Santa santa = new Santa();
			st = new StringTokenizer(br.readLine());
			santa.num = Integer.parseInt(st.nextToken());
			santa.r = Integer.parseInt(st.nextToken());
			santa.c = Integer.parseInt(st.nextToken());
			map[santa.r][santa.c] = santa.num;
			santas[santa.num] = santa;
		}
		
		for (int i = 0; i < M; i++) {
			Santa santa = getPriority();
			
			if(santa == null) break;
			
			int d = nearestDir(santa, 8);
			
			int nr = Santa.rudolf_r + deltas[d][0];
			int nc = Santa.rudolf_c + deltas[d][1];
			if(map[nr][nc] != 0) {
				santa.kijul = i+1;
				santa.jumsu += C;
				move(map[nr][nc], d, C, i);
			}
			map[Santa.rudolf_r][Santa.rudolf_c] = 0;
			map[nr][nc] = P+1;
			Santa.rudolf_r = nr;
			Santa.rudolf_c = nc;
			
			// 산타 이동
			int cnt = 0;
			for (int j = 1; j <= P; j++) {
				santa = santas[j];
				if(santa.isDead) {
					cnt++;
					continue;
				}
				if(santa.kijul !=0 && (santa.kijul == i || santa.kijul == i+1)) continue;
				santa.kijul = 0;
				
				d = nearestDir(santa, 4);
				
				if(d == -1) continue;
				nr = santa.r + deltas[d][0];
				nc = santa.c + deltas[d][1];
				map[santa.r][santa.c] = 0;
				santa.r = nr;
				santa.c = nc;
				if(map[nr][nc] == P+1) {
					santa.kijul = i+1;
					santa.jumsu += D;
					d = (d + 2) % 4;
					move(j, d, D, i);
				}
				else {
					map[nr][nc] = j;
				}
			}
			if(cnt == P) break;
			
		}
		
		StringBuilder sb = new StringBuilder();
		for (int j = 1; j <= P; j++) {
			if(!santas[j].isDead) santas[j].jumsu += M;
			sb.append(santas[j].jumsu).append(" ");
		}
		System.out.println(sb);
	}


	private static Santa getPriority() {
		pq.clear();
		for (int j = 1; j <= P; j++) {
			if(!santas[j].isDead) pq.offer(santas[j]);
		}
		return pq.isEmpty()? null : pq.poll();
	}

	private static void move(int num, int d, int size, int turn) {
		queue.clear();
		int nr = santas[num].r + (deltas[d][0]*size);
		int nc = santas[num].c + (deltas[d][1]*size);

		if(nr < 1 || nr > N || nc < 1 || nc > N) {
			santas[num].isDead = true;
			santas[num].jumsu += turn;
			return;
		}
		
		if(map[nr][nc] != 0) {
			queue.offer(santas[map[nr][nc]]);
		}
		map[nr][nc] = santas[num].num;
		santas[num].r = nr;
		santas[num].c = nc;
		
		Santa santa;
		while(!queue.isEmpty()) {
			santa = queue.poll();
			nr = santa.r + deltas[d][0];
			nc = santa.c + deltas[d][1];
			if(nr < 1 || nr > N || nc < 1 || nc > N) {
				santa.isDead = true;
				santa.jumsu += turn;
				continue;
			}
			
			if(map[nr][nc] != 0) {
				queue.offer(santas[map[nr][nc]]);
			}
			map[nr][nc] = santa.num;
			santa.r = nr;
			santa.c = nc;
		}
	}

	private static int nearestDir(Santa santa, int iter) {
		int dir = -1;
		int r=0, c=0, or=0, oc=0;
		if(iter == 4) {
			r = santa.r;
			c = santa.c;
			or = Santa.rudolf_r;
			oc = Santa.rudolf_c;
		}
		else {
			or = santa.r;
			oc = santa.c;
			r = Santa.rudolf_r;
			c = Santa.rudolf_c;
		}
		
		int minDistance = (r-or)*(r-or) + (c-oc)*(c-oc);
		for (int d = 0; d < iter; d++) {
			int nr = r + deltas[d][0];
			int nc = c + deltas[d][1];
			
			if(nr < 1 || nr > N || nc < 1 || nc > N) continue;
			if(iter == 4 && map[nr][nc] > 0 && map[nr][nc] <= P) continue;
			
			int dis = (nr-or)*(nr-or) + (nc-oc)*(nc-oc);
			if(minDistance > dis) {
				minDistance = dis;
				dir = d;
			}
		}
		
		return dir;
	}

}