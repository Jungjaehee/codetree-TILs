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
	static int K, M;
	static int[][] map;
	static int[][] turnMap;
	static int[][] tmp;
	static int[] wall;
	static int index;
	static Queue<Position> queue;
	static int[][] deltas = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
	static class Position implements Comparable<Position> {
		int r;
		int c;
		
		public Position(int r, int c) {
			super();
			this.r = r;
			this.c = c;
		}

		@Override
		public int compareTo(Position o) {
			return this.c == o.c? -Integer.compare(this.r, o.r):Integer.compare(this.c, o.c);
		}
	}
	
	static class Node implements Comparable<Node>{
		int r;
		int c;
		int degree;
		List<Position> pieces;
		
		public Node(int r, int c, int degree) {
			this.r = r;
			this.c = c;
			this.degree = degree;
		}

		@Override
		public int compareTo(Node o) {
			if(this.pieces.size() == o.pieces.size()) {
				if(this.degree == o.degree) {
					if(this.c == o.c) {
						return Integer.compare(this.r, o.r);
					}
					return Integer.compare(this.c, o.c);
				}
				return Integer.compare(this.degree, o.degree);
			}
			return -Integer.compare(this.pieces.size(), o.pieces.size());
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br  = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		StringBuffer sb = new StringBuffer();
		
		K = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		map = new int[5][5];
		turnMap = new int[5][5];
		tmp = new int[3][3];
		wall = new int[M];
		index = 0;
		queue = new ArrayDeque<>();
		
		for (int i = 0; i < 5; i++) {
			st =  new StringTokenizer(br.readLine());
			for (int j = 0; j < 5; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		st = new StringTokenizer(br.readLine());
		for (int i = 0; i < M; i++) {
			wall[i] = Integer.parseInt(st.nextToken());
		}
		
		List<Node> nodes = new ArrayList<>();
		
		for (int i = 0; i < K; i++) {
			nodes.clear();
			int totalSum = 0;
			// 1. 탐사 진행
			for (int j = 1; j < 4; j++) {
				for (int k = 1; k < 4; k++) {
					mapToTurnMap();
					for (int z = 90; z <= 270; z+=90) {
						Node node = new Node(j, k, z);
						turn90Degree(j, k, turnMap);
						getPieces(node, turnMap);
						if(node.pieces.size() > 0) nodes.add(node);
					}
				}
			}

			if(nodes.isEmpty()) break;
			
			Collections.sort(nodes);
		
			Node node = nodes.get(0);
			totalSum += node.pieces.size();
			
			// 2. 유물 획득
			int turn = node.degree / 90;
			for (int j = 0; j < turn ; j++) {
				turn90Degree(node.r, node.c, map);					
			}

			while(true) {
				fillWallNum(node);
				getPieces(node, map);
				if(node.pieces.isEmpty()) break;
				totalSum += node.pieces.size();
			}
			sb.append(totalSum).append(" ");
		}
		
		System.out.println(sb);
	}
	
	static void mapToTurnMap() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				turnMap[i][j] = map[i][j];
			}
		}
	}
	

	
	static void turn90Degree(int i, int j, int[][] target) {
		// (j, 2-i)
		for (int r = 0, ii = i-1; r < 3; r++, ii++) {
			for (int c = 0, jj = j-1; c < 3; c++, jj++) {
				tmp[r][c] = target[ii][jj];
			}
		}
		
		for (int r = 0, ii = i-1; r < 3; r++, ii++) {
			for (int c = 0, jj = j-1; c < 3; c++, jj++) {
				target[ii][jj] = tmp[2-c][r];
			}
		}

	}
	
	static void getPieces(Node node, int[][] target) {
		int isVisited = 0;
		node.pieces = new ArrayList<>();
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if((isVisited & (1 << (i*5 + j))) != 0) continue;
				int num = target[i][j];
				isVisited |= (1 << (i*5 + j));
				Position start = new Position(i, j); 
				queue.offer(start);
				List<Position> pieces = new ArrayList<>();
				pieces.add(start);
				while(!queue.isEmpty()) {
					Position pos = queue.poll();
					for (int d = 0; d < 4; d++) {
						int nr = pos.r + deltas[d][0];
						int nc = pos.c + deltas[d][1];
						if(nr < 0 || nr > 4 || nc <0 || nc > 4) continue;
						if((isVisited & (1 << (nr*5 + nc))) != 0 || target[nr][nc] != num) continue;
						
						pos = new Position(nr, nc);
						queue.offer(pos);
						pieces.add(pos);
						isVisited |= (1 << (nr*5 + nc));
					}
				}
				
				if(pieces.size() > 2) {
					node.pieces.addAll(pieces);
				}
			}
		}
	}
	
	static void fillWallNum(Node node) {
		Collections.sort(node.pieces);
		for (Position pos : node.pieces) {
			map[pos.r][pos.c] = wall[index++];
		}
	}

}