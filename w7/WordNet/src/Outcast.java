/* *****************************************************************************
 *  Name: Pascal P
 *  Date: May 2020
 *  Description:
 **************************************************************************** */

public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) { // constructor takes a WordNet object
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) { // given an array of WordNet nouns, return an outcast
        if (nouns == null)
            throw new IllegalArgumentException("not nouns!");

        String outcast = "";
        int maxDist = Integer.MIN_VALUE;

        for (String noun : nouns) {
            if (noun == null)
                throw new IllegalArgumentException("not a noun!");

            int cdist = 0;
            for (String onoun : nouns) {
                if (onoun == null)
                    throw new IllegalArgumentException("not a noun!");
                if (onoun == noun) continue;
                cdist += wordnet.distance(noun, onoun);
            }

            // cmp
            if (cdist > maxDist) {
                maxDist = cdist;
                outcast = noun;
            }
        }
        return outcast;
    }

    public static void main(String[] args) {  // see test client below
        // TODO
    }
}


