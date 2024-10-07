import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

    static class Pos {

        int r, c;

        public Pos(int r, int c) {
            super();
            this.r = r;
            this.c = c;
        }
    }

    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, 1, 0, -1};

    static int[][] down = {{1, -1}, {1, 1}, {2, 0}};
    static int[][] left = {{-1, -1}, {0, -2}, {1, -2}, {1, -1}, {2, -1}};
    static int[][] right = {{-1, 1}, {0, 2}, {1, 2}, {1, 1}, {2, 1}};

    static int R, C;
    static final int offset = 3;
    static int[][] map;
    static int cnt;

    static Queue<Pos> center;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken()); // 행
        C = Integer.parseInt(st.nextToken()); // 열
        int K = Integer.parseInt(st.nextToken()); // 정령 수
        map = new int[R + 1 + offset][C + 1];
        center = new ArrayDeque<>();

        for (int i = 1; i <= K; i++) {
            st = new StringTokenizer(br.readLine());
            int c = Integer.parseInt(st.nextToken()); // 중앙 열
            int d = Integer.parseInt(st.nextToken()); // 출구 방향

            // 골렘 남쪽으로 내려가기
            int r = offset - 1;
            while (true) {
                if (checkDown(r, c)) {
                    r++;
                } else if (checkLeft(r, c)) {
                    r++;
                    c--;
                    d = (d - 1) % 4; // 방향 감소
                } else if (checkRight(r, c)) {
                    r++;
                    c++;
                    d = (d + 1) % 4; // 방향 증가
                } else {
                    break;
                }
            }

            // 골렘이 숲 밖으로 삐져나온 경우
            if (r <= offset) {
                // 현재 맵에서 정령 남쪽으로 모두 이동시키기
                moveDown();
                // 현재 맵 비우기
                map = new int[R + 1 + offset][C + 1];
            }
            // 골렘이 숲 안으로 잘 들어간 경우
            else {
                update(r, c, d, i);
            }

        }

        moveDown();
        System.out.println(cnt);
    }

    private static void update(int r, int c, int d, int num) {
        map[r][c] = num;
        center.add(new Pos(r, c));
        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            map[nr][nc] = num; // 각 골렘정보는 숫자로 구분한다.
            if (i == d) {
                map[nr][nc] *= -1; // 출구는 동일한 숫자를 음수형태로 저장한다.
            }
        }
    }

    private static void moveDown() {
        // 현재 맵에서 정령 남쪽으로 모두 이동시키기
        while (!center.isEmpty()) {
            Pos pos = center.poll();
            cnt += moveDown(pos);
        }
    }

    private static int moveDown(Pos pos) {
        int maxRow = 0;
        Queue<Pos> q = new ArrayDeque<>();
        q.add(pos);
        boolean[][] visited = new boolean[R + 1 + offset][C + 1];
        visited[pos.r][pos.c] = true;

        while (!q.isEmpty()) {
            Pos now = q.poll();
            maxRow = Math.max(maxRow, now.r - offset);

            for (int i = 0; i < 4; i++) {
                int nr = now.r + dr[i];
                int nc = now.c + dc[i];

                // 범위를 벗어났거나, 골렘이 아니거나, 이미 방문한 곳이면 안된다.
                if (!range(nr, nc) || map[nr][nc] == 0 || visited[nr][nc]) {
                    continue;
                }
                // 다음위치가 다른골렘인데 지금위치가 출구가 아니라면(음수가 아니라면) 이동불가능하다.
                if ((Math.abs(map[now.r][now.c]) != Math.abs(map[nr][nc]))
                    && map[now.r][now.c] >= 0) {
                    continue;
                }

                q.add(new Pos(nr, nc));
                visited[nr][nc] = true;
            }
        }

        return maxRow;
    }


    static boolean checkDown(int r, int c) {
        for (int i = 0; i < down.length; i++) {
            int nr = r + down[i][0];
            int nc = c + down[i][1];
            if (!range(nr, nc)) {
                return false;
            }
            if (map[nr][nc] != 0) {
                return false;
            }
        }
        return true;
    }

    static boolean checkLeft(int r, int c) {
        for (int i = 0; i < left.length; i++) {
            int nr = r + left[i][0];
            int nc = c + left[i][1];
            if (!range(nr, nc)) {
                return false;
            }
            if (map[nr][nc] != 0) {
                return false;
            }
        }
        return true;
    }

    static boolean checkRight(int r, int c) {
        for (int i = 0; i < right.length; i++) {
            int nr = r + right[i][0];
            int nc = c + right[i][1];
            if (!range(nr, nc)) {
                return false;
            }
            if (map[nr][nc] != 0) {
                return false;
            }
        }
        return true;
    }

    static boolean range(int r, int c) {
        return r > 0 && c > 0 && r <= R + offset && c <= C;
    }

}