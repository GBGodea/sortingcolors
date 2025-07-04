package main.java.SortingColorsGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class SortingColorsGame {
    private final int height;
    private final int tubeCount;
    private static final int MAX_DEPTH = 100;
    private Set<String> visited = new HashSet<>();

 /*   int[][] initial = {
                { 2,  1, 10,  5,  6, 12,  4, 10, 10,  6,  4,  5,  0,  0 },
                { 10,  8,  7,  3, 11, 12,  7, 11,  7,  2,  6,  3,  0,  0 },
                { 4, 12,  5,  2,  8,  1,  8,  3,  9,  6,  9, 12,  0,  0 },
                { 4,  8,  9,  5,  7,  2, 11,  1,  9, 11,  3,  1,  0,  0 }
    };

*/
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите количество колб");
        int count = Integer.parseInt(reader.readLine());
        System.out.println("Введите высоту колб");
        int heightColb = Integer.parseInt(reader.readLine());

        int[][] initial = new int[heightColb][count];

        for(int i = 0; i < count; i++) {
            System.out.printf("Введите колбу No %d%n", i+1);
            for(int j = 0; j < heightColb; j++) {
                initial[j][i] = Integer.parseInt(reader.readLine());
            }
        }

        int height = initial.length;
        int tubeCount = initial[0].length;

        List<Stack<Integer>> tubes = new ArrayList<>(tubeCount);
        for (int j = 0; j < tubeCount; j++) {
            Stack<Integer> tube = new Stack<>();
            for (int i = height - 1; i >= 0; i--) {
                int color = initial[i][j];
                if (color != 0) {
                    tube.push(color);
                }
            }
            tubes.add(tube);
        }

        SortingColorsGame solver = new SortingColorsGame(height, tubeCount);
        List<int[]> solution = solver.solve(tubes);

        if (solution == null) {
            System.out.println("No.");
        } else {
            System.out.println("Решение найдено за " + solution.size() + " шагов:");
            for (int[] move : solution) {
                System.out.println(Arrays.toString(move));
            }
        }
    }

    public SortingColorsGame(int height, int tubeCount) {
        this.height = height;
        this.tubeCount = tubeCount;
    }

    public List<int[]> solve(List<Stack<Integer>> initialTubes) {
        return dfs(new State(initialTubes, new ArrayList<>()), 0);
    }

    private List<int[]> dfs(State state, int depth) {
        if (state.isSorted()) {
            return state.moves;
        }
        if (depth >= MAX_DEPTH) {
            return null;
        }

        String key = state.serialize();
        if (visited.contains(key)) {
            return null;
        }
        visited.add(key);

        for (int from = 0; from < tubeCount; from++) {
            if (state.tubes.get(from).isEmpty()) continue;
            for (int to = 0; to < tubeCount; to++) {
                if (from == to) continue;
                if (!canPour(state.tubes.get(from), state.tubes.get(to))) continue;

                State next = new State(state.tubes, state.moves);
                pour(next.tubes.get(from), next.tubes.get(to));
                next.moves.add(new int[]{from, to});

                List<int[]> result = dfs(next, depth + 1);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private boolean canPour(Stack<Integer> from, Stack<Integer> to) {
        if (to.size() == height) return false;
        if (to.isEmpty())      return true;
        return from.peek().equals(to.peek());
    }

    private void pour(Stack<Integer> from, Stack<Integer> to) {
        int color = from.peek();
        while (!from.isEmpty() && from.peek() == color && to.size() < height) {
            to.push(from.pop());
        }
    }

    static class State {
        List<Stack<Integer>> tubes;
        List<int[]> moves;

        State(List<Stack<Integer>> tubes, List<int[]> moves) {
            this.tubes = new ArrayList<>();
            for (Stack<Integer> s : tubes) {
                Stack<Integer> copy = new Stack<>();
                copy.addAll(s);
                this.tubes.add(copy);
            }
            this.moves = new ArrayList<>(moves);
        }

        boolean isSorted() {
            for (Stack<Integer> tube : tubes) {
                if (!tube.isEmpty()) {
                    int c = tube.peek();
                    for (int x : tube) if (x != c) return false;
                }
            }
            return true;
        }

        String serialize() {
            StringBuilder sb = new StringBuilder();
            for (Stack<Integer> t : tubes) {
                sb.append(t).append("|");
            }
            return sb.toString();
        }
    }
}
