package modernJava;

public class TourGuide {

    public static int getBestSightseeingPairScore(int[] places){
        int best=0;
        for(int i=0; i< places.length-1; ++i){
            for(int j=1; j< places.length;++j){
                best= Math.max(best, places[i] + places[j] + i - j);
            }
        }

        return best;
    }

    public static void main(String[] args) {
        System.out.println(getBestSightseeingPairScore(new int[]{8, 1, 5, 2, 6}));
        System.out.println(getBestSightseeingPairScore(new int[]{1, 2}));
    }
}
