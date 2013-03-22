package org.ifomis.ontologyaggregator.recommendation;

import java.util.Arrays;

/**
 * @author http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Dice's_coefficient#Java
 *
 */
public class DiceCoefficient {

	/**
	 * Here's an optimized version of the dice coefficient calculation. It takes
	 * advantage of the fact that a bigram of 2 chars can be stored in 1 int,
	 * and applies a matching algorithm of O(n*log(n)) instead of O(n*n).
	 * 
	 * <p>
	 * Note that, at the time of writing, this implementation differs from the
	 * other implementations on this page. Where the other algorithms
	 * incorrectly store the generated bigrams in a set (discarding duplicates),
	 * this implementation actually treats multiple occurrences of a bigram as
	 * unique. The correctness of this behavior is most easily seen when getting
	 * the similarity between "GG" and "GGGGGGGG", which should obviously not be
	 * 1.
	 * 
	 * @param s
	 *            The first string
	 * @param t
	 *            The second String
	 * @return The dice coefficient between the two input strings. Returns 0 if
	 *         one or both of the strings are {@code null}. Also returns 0 if
	 *         one or both of the strings contain less than 2 characters and are
	 *         not equal.
	 * @author Jelle Fresen
	 */
	public static double diceCoefficientOptimized(String s, String t) {
		// Verifying the input:
		if (s == null || t == null)
			return 0;
		// Quick check to catch identical objects:
		if (s == t)
			return 1;
		// avoid exception for single character searches
		if (s.length() < 2 || t.length() < 2)
			return 0;

		// Create the bigrams for string s:
		final int n = s.length() - 1;
		final int[] sPairs = new int[n];
		for (int i = 0; i <= n; i++)
			if (i == 0)
				sPairs[i] = s.charAt(i) << 16;
			else if (i == n)
				sPairs[i - 1] |= s.charAt(i);
			else
				sPairs[i] = (sPairs[i - 1] |= s.charAt(i)) << 16;

		// Create the bigrams for string t:
		final int m = t.length() - 1;
		final int[] tPairs = new int[m];
		for (int i = 0; i <= m; i++)
			if (i == 0)
				tPairs[i] = t.charAt(i) << 16;
			else if (i == m)
				tPairs[i - 1] |= t.charAt(i);
			else
				tPairs[i] = (tPairs[i - 1] |= t.charAt(i)) << 16;

		// Sort the bigram lists:
		Arrays.sort(sPairs);
		Arrays.sort(tPairs);

		// Count the matches:
		int matches = 0, i = 0, j = 0;
		while (i < n && j < m) {
			if (sPairs[i] == tPairs[j]) {
				matches += 2;
				i++;
				j++;
			} else if (sPairs[i] < tPairs[j])
				i++;
			else
				j++;
		}
		return (double) matches / (n + m);
	}
}
