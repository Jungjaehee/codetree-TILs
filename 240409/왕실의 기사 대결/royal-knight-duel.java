import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
	static int L;
	static int N;
	static int[][] knight;
	static int[][] map;
	static int[][] knight_map;
	static int[][] deltas = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	static List<Integer> list;
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		int Q = Integer.parseInt(st.nextToken());
		
		list = new ArrayList<>(N+1);
		knight = new int[N+1][6];
		map = new int[L+1][L+1];
		knight_map = new int[L+1][L+1];
		
		for (int i = 1; i < L+1; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j < L+1; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			knight[i][0] = Integer.parseInt(st.nextToken());
			knight[i][1] = Integer.parseInt(st.nextToken());
			knight[i][2] = knight[i][0] + Integer.parseInt(st.nextToken());
			knight[i][3] = knight[i][1] + Integer.parseInt(st.nextToken());
			knight[i][4] = Integer.parseInt(st.nextToken());
			knight[i][5] = knight[i][4];
			
			for (int j = knight[i][0]; j < knight[i][2]; j++) {
				for (int k = knight[i][1]; k < knight[i][3]; k++) {
					knight_map[j][k] = i;
				}
			}
		}
		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int n = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			
			boolean[] isMove = new boolean[N+1];
			if(knight[n][4] < 1 || !movePossible(n, d, isMove)) continue;
			move(n, d);
			kill(n, isMove);
			
		}
		
		int sum = 0;
		for (int i = 1; i <= N; i++) {
			if(knight[i][4] > 0) sum+= (knight[i][5]-knight[i][4]); 
		}
		
		System.out.println(sum);
		
	}
	
	private static void kill(int n, boolean[] isMove) {
		for (int i = 1; i <= N; i++) {
			if(!isMove[i]) continue;
			for (int j = knight[i][0]; j < knight[i][2]; j++) {
				for (int k = knight[i][1]; k < knight[i][3]; k++) {
					if(map[j][k] == 1) knight[i][4]--;
				}
			}
			
			if(knight[i][4] < 1) {
				for (int j = knight[i][0]; j < knight[i][2]; j++) {
					for (int k = knight[i][1]; k < knight[i][3]; k++) {
						knight_map[j][k] = 0;
					}
				}
			}
		}
	}
	private static void move(int n, int d) {
		while(!list.isEmpty()) {
			int idx = list.remove(list.size()-1);
			int r = knight[idx][0] + deltas[d][0];
			int c = knight[idx][1] + deltas[d][1];
			
			int rh = knight[idx][2] + deltas[d][0];
			int cw = knight[idx][3] + deltas[d][1];
			
			for (int i = knight[idx][0]; i < knight[idx][2]; i++) {
				for (int j = knight[idx][1]; j < knight[idx][3]; j++) {
					if(knight_map[i][j] == idx) knight_map[i][j] = 0;
				}
			}
			for (int i = r; i < rh; i++) {
				for (int j = c; j < cw; j++) {
					knight_map[i][j] = idx;
				}
			}
			
			knight[idx][0] = r;
			knight[idx][1] = c;
			knight[idx][2] = rh;
			knight[idx][3] = cw;
		}
		
	}
	private static boolean movePossible(int n, int d, boolean[] isVisited) {
		list.clear();
		list.add(n);
		int idx = 0;
		while(list.size() > idx) {
			int num = list.get(idx++);
			int r = knight[num][0] + deltas[d][0];
			int c = knight[num][1] + deltas[d][1];
			
			int rh = knight[num][2] + deltas[d][0];
			int cw = knight[num][3] + deltas[d][1];
			
			if(r < 1 || r > L || c < 1 || c > L) return false;
			if(rh < 0 || rh > L + 1 || cw < 0 || cw > L + 1) return false;
			
			for (int i = r; i < rh; i++) {
				for (int j = c; j < cw; j++) {
					if(map[i][j] == 2) return false;
					if(knight_map[i][j] != 0 && knight_map[i][j] != num) {
						int next = knight_map[i][j];
						if(!isVisited[next]) {
							isVisited[next] = true;
							list.add(next);
						}
					}
				}
			}
		}
		return true;
	}

}