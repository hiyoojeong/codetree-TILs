import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

// 고대 문명 유적 탐사
public class Main {

    static class Pos {

        int r, c;

        public Pos(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public String toString() {
            return "Pos{" +
                "r=" + r +
                ", c=" + c +
                '}';
        }
    }

    static class Info implements Comparable<Info> {

        int value, rotate, r, c;

        public Info(int value, int rotate, int r, int c) {
            this.value = value;
            this.rotate = rotate;
            this.r = r;
            this.c = c;
        }

        @Override
        public int compareTo(Info o) {
            if (this.value == o.value) {
                if (this.rotate == o.rotate) {
                    if (this.r == o.r) {
                        return Integer.compare(this.c, o.c);
                    }
                    return Integer.compare(this.r, o.r);
                }
                return Integer.compare(this.rotate, o.rotate);
            }
            return -Integer.compare(this.value, o.value);
        }

        @Override
        public String toString() {
            return "Info{" +
                "value=" + value +
                ", rotate=" + rotate +
                ", r=" + r +
                ", c=" + c +
                '}';
        }
    }

    static final int N = 5, M = 3;
    static int[][] map = new int[N][N];
    static int[][] tmp = new int[N][N];
    static int[] dr = {-1, 1, 0, 0};
    static int[] dc = {0, 0, -1, 1};
    static Queue<Integer> next = new ArrayDeque<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        StringBuilder answer = new StringBuilder();

        st = new StringTokenizer(br.readLine());
        int K = Integer.parseInt(st.nextToken()); // 탐사 반복횟수
        int M = Integer.parseInt(st.nextToken()); // 벽면에 적인 유물 조각의 개수

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            next.add(Integer.parseInt(st.nextToken()));
        }

        for (int i = 0; i < K; i++) {
            // 탐사진행
            rotate();

            // 유물연쇄획득
            List<Pos> parts = null;
            int cnt = 0;
            while ((parts = findParts()).size() > 0) {
                // 유물획득
                for (Pos part : parts) {
                    cnt += getCnt(new ArrayDeque<>(), new boolean[N][N], part.r, part.c, true);
                }
                // 빈공간 채우기
                fill();
            }

            if (cnt == 0) { // 더이상 탐사해도 유물이 나오지 않음
                break;
            }

            answer.append(cnt + " ");
        }

        System.out.println(answer);
    }

    private static void print() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void fill() {
        for (int j = 0; j < N; j++) {
            for (int i = N - 1; i >= 0; i--) {
                if (map[i][j] == 0) {
                    map[i][j] = next.poll();
                }
            }
        }
    }

    private static List<Pos> findParts() {
        List<Pos> parts = new ArrayList<>();

        Queue<Pos> q = new ArrayDeque<>();
        boolean[][] visited = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!visited[i][j]) {
                    int cnt = getCnt(q, visited, i, j, false);
                    if (cnt >= 3) {
                        parts.add(new Pos(i, j));
                    }
                }
            }
        }

        return parts;
    }

    private static void rotate() {
        // 모든 회전방법을 확인해본다.
        Queue<Info> pq = new PriorityQueue<>();
        for (int i = 1; i < N - 1; i++) {
            for (int j = 1; j < N - 1; j++) {
                for (int k = 1; k <= 3; k++) { // 회전 수
                    rotate(i, j);
                    pq.add(new Info(getValue(), k, i, j));
                }
                rotate(i, j);
            }
        }

        // 회전방법을 선택하고 회전한다.
        Info select = pq.poll();
        // System.out.println("회전하는 위치 : " + select);

        for (int i = 0; i < select.rotate; i++) {
            rotate(select.r, select.c);
        }
    }

    private static void rotate(int r, int c) {
        r--;
        c--;
        // tmp에 회전한 정보 저장
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                tmp[j + r][(M - 1) - i + c] = map[i + r][j + c];
            }
        }

        // tmp에 저장한 정보를 map에 다시 불러오기
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                map[i + r][j + c] = tmp[i + r][j + c];
            }
        }
    }

    private static int getValue() {
        Queue<Pos> q = new ArrayDeque<>();
        boolean[][] visited = new boolean[N][N];
        int result = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!visited[i][j]) {
                    int cnt = getCnt(q, visited, i, j, false);
                    if (cnt >= 3) {
                        result += cnt;
                    }
                }
            }
        }
        return result;
    }

    private static int getCnt(Queue<Pos> q, boolean[][] visited, int i, int j, boolean isRemove) {
        int cnt = 0;

        q.add(new Pos(i, j));
        visited[i][j] = true;

        while (!q.isEmpty()) {
            Pos now = q.poll();
            cnt++;

            for (int d = 0; d < 4; d++) {
                int nr = now.r + dr[d];
                int nc = now.c + dc[d];
                if (nr < 0 || nc < 0 || nr >= N || nc >= N) {
                    continue;
                }
                if (visited[nr][nc] || map[nr][nc] != map[now.r][now.c]) {
                    continue;
                }

                q.add(new Pos(nr, nc));
                visited[nr][nc] = true;
            }

            if (isRemove) {
                map[now.r][now.c] = 0;
            }
        }
        return cnt;
    }
}