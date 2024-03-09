import java.io.*;
import java.util.*;

class Candidate {
    private String name;

    public Candidate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Voter {
    private String voterId;

    public Voter(String voterId) {
        this.voterId = voterId;
    }

    public String getVoterId() {
        return voterId;
    }
}

class ElectionManager {
    private Map<String, Integer> votes = new HashMap<>();
    private static final String WINNER_FILE = "winner.txt";

    public void castVote(String candidateName) {
        votes.put(candidateName, votes.getOrDefault(candidateName, 0) + 1);
    }

    public Map<String, Integer> getResults() {
        return new HashMap<>(votes);
    }

    public String getWinner() {
        int maxVotes = 0;
        String winner = null;

        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winner = entry.getKey();
            }
        }

        if (winner != null) {
            saveWinnerToFile(winner);
        }

        return winner;
        
    }

    private void saveWinnerToFile(String winner) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WINNER_FILE, true))) {
            writer.println("Winner: " + winner);
        } catch (IOException e) {
            System.err.println("Error saving winner to file: " + e.getMessage());
        }
    }
}

class CandidateManager {
    private Map<String, Candidate> candidates = new HashMap<>();
    private static final String CANDIDATE_FILE = "candidates.txt";

    public CandidateManager() {
        loadCandidatesFromFile();
    }

    private void loadCandidatesFromFile() {
        try (Scanner scanner = new Scanner(new File(CANDIDATE_FILE))) {
            while (scanner.hasNext()) {
                String candidateName = scanner.next();
                candidates.put(candidateName, new Candidate(candidateName));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Candidates file not found. Creating a new one.");
        }
    }

    public void addCandidate(String name) {
        candidates.put(name, new Candidate(name));
        saveCandidatesToFile();
    }

    public void displayCandidates() {
        System.out.println("Candidates:");
        for (Candidate candidate : candidates.values()) {
            System.out.println(candidate.getName());
        }
    }

    public Candidate getCandidate(String name) {
        return candidates.get(name);
    }

    private void saveCandidatesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CANDIDATE_FILE))) {
            for (Candidate candidate : candidates.values()) {
                writer.println(candidate.getName());
            }
        } catch (IOException e) {
            System.err.println("Error saving candidates to file: " + e.getMessage());
        }
    }
}

class VoterManager {
    private Map<String, Voter> voters = new HashMap<>();
    private static final String VOTER_FILE = "voters.txt";

    public VoterManager() {
        loadVotersFromFile();
    }

    private void loadVotersFromFile() {
        try (Scanner scanner = new Scanner(new File(VOTER_FILE))) {
            while (scanner.hasNext()) {
                String voterId = scanner.next();
                voters.put(voterId, new Voter(voterId));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Voters file not found. Creating a new one.");
        }
    }

    public void addVoter(String voterId) {
        voters.put(voterId, new Voter(voterId));
        saveVotersToFile();
    }

    public boolean hasVoted(String voterId) {
        return voters.containsKey(voterId);
    }

    private void saveVotersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VOTER_FILE))) {
            for (Voter voter : voters.values()) {
                writer.println(voter.getVoterId());
            }
        } catch (IOException e) {
            System.err.println("Error saving voters to file: " + e.getMessage());
        }
    }
}

class VotingSystem {
    private static final String RESULTS_FILE = "results.txt";
    private CandidateManager candidateManager = new CandidateManager();
    private VoterManager voterManager = new VoterManager();
    private ElectionManager electionManager = new ElectionManager();

    public void addCandidate(String name) {
        candidateManager.addCandidate(name);
    }

    public void displayCandidates() {
        candidateManager.displayCandidates();
    }

    public void vote(String candidateName, String voterId) {
        if (!voterManager.hasVoted(voterId)) {
            Candidate candidate = candidateManager.getCandidate(candidateName);
            if (candidate != null) {
                electionManager.castVote(candidate.getName());
                voterManager.addVoter(voterId);
                System.out.println("Vote cast successfully for " + candidate.getName());
            } else {
                System.out.println("Invalid candidate!");
            }
        } else {
            System.out.println("You have already voted!");
        }
    }

    public void displayResults() {
        System.out.println("Election Results:");
        Map<String, Integer> results = electionManager.getResults();
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " votes");
        }
        saveResultsToFile(results);
    }

    private void saveResultsToFile(Map<String, Integer> results) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESULTS_FILE))) {
            for (Map.Entry<String, Integer> entry : results.entrySet()) {
                writer.println(entry.getKey() + ": " + entry.getValue() + " votes");
            }
        } catch (IOException e) {
            System.err.println("Error saving results to file: " + e.getMessage());
        }
    }

    public String getWinner() {
        return electionManager.getWinner();
    }
}

public class Main {
    public static void main(String[] args) {
        VotingSystem votingSystem = new VotingSystem();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("1. Add Candidate");
            System.out.println("2. Display Candidates");
            System.out.println("3. Vote");
            System.out.println("4. Display Results");
            System.out.println("5. Get Winner");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter candidate name: ");
                        String candidateName = scanner.nextLine();
                        votingSystem.addCandidate(candidateName);
                        break;
                    case 2:
                        votingSystem.displayCandidates();
                        break;
                    case 3:
                        System.out.print("Enter candidate name: ");
                        String candidate = scanner.nextLine();
                        System.out.print("Enter your voter ID: ");
                        String voterId = scanner.nextLine();
                        votingSystem.vote(candidate, voterId);
                        break;
                    case 4:
                        votingSystem.displayResults();
                        break;
                    case 5:
                        String winner = votingSystem.getWinner();
                        if (winner != null) {
                            System.out.println("The winner is: " + winner);
                        } else {
                            System.out.println("No winner yet.");
                        }
                        break;
                    case 6:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice!");
                        break;
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Consume invalid input
            }
        }

        scanner.close();
    }
}
