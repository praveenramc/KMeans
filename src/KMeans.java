/*** Author :Vibhav Gogate, Praveen Ram Chandiran
The University of Texas at Dallas
*****/

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class KMeans {
  public static void main( String [] args ) {
  if ( args.length < 3 ) {
    System.out.println( "Usage: Kmeans <input-image> <k> <output-image>" );
    return;
  }
  
  Long a = new Long(5);
  a.byteValue();

  try {
    // Read original image from file, run through kmeans, and output results.
    BufferedImage originalImage = ImageIO.read( new File(args[0]) );
    BufferedImage kmeansJpg = kmeans_helper( originalImage, Integer.parseInt(args[1]) );
    ImageIO.write( kmeansJpg, "png", new File(args[2]) );
  }

  catch ( IOException e ) {
    System.out.println( e.getMessage() );
  }
  }

  /**
   * @param originalImage - An image to compress
   * @param k - The number of clusters to use to compress originalImage
   * @return A compressed image using the kmeans algorithm
   */
  private static BufferedImage kmeans_helper( BufferedImage originalImage, int k ) {
  int w = originalImage.getWidth();
  int h = originalImage.getHeight();
  BufferedImage kmeansImage = new BufferedImage( w, h, originalImage.getType() );
  Graphics2D g = kmeansImage.createGraphics();
  g.drawImage( originalImage, 0, 0, w, h, null );

  // Read rgb values from the image.
  int[] imageRGB = new int[(w*h)];
  int counter = 0;
  for ( int iterator = 0; iterator < w; iterator++ ) {
    for( int innerLooper = 0; innerLooper < h; innerLooper++ ) {
         imageRGB[counter++] = kmeansImage.getRGB(iterator,innerLooper);
    }
  }

  // Call kmeans algorithm: update the rgb values to compress image.
  kmeans( imageRGB,k );

  // Write the new rgb values to the image.
  counter = 0;
  for( int iterator = 0; iterator < w; iterator++ ) {
    for( int innerLooper = 0; innerLooper < h; innerLooper++ ) {
        kmeansImage.setRGB( iterator, innerLooper, imageRGB[counter++] );
    }
  }

  // Return the compressed image
  return kmeansImage;
  }

  //Your k-means code goes here
  // Update the array rgb by assigning each entry in the rgb array to its cluster center
  private static void kmeans( int[] pixels, int k ) {

    int[] previousCentroids = new int[k];   
    int[] currentCentroids = new int[k];   
    int[] noOfPixels = new int[k];  
    int[] noOfRedInCluster = new int[k];   
    int[] noOfGreenInCluster = new int[k]; 
    int[] noOfBlueInCluster = new int[k];  
    int[] clusterAssignment = new int[pixels.length]; 
    int counter = 0;     //Dummy variable for testing purpose. Remove it before submitting       

    double maximumDistance = Double.MAX_VALUE;   
    double currentDistance = 0;                   
    int closestCenter = 0;               
    
   
    for ( int iterator = 0; iterator < k; iterator++ ) {
      Random random = new Random();
      int centerValue = 0;
      centerValue = pixels[random.nextInt( pixels.length )];
      currentCentroids[iterator] = centerValue;
    }

 
    do {
      for ( int iterator = 0; iterator < currentCentroids.length; iterator++ ) {
        previousCentroids[iterator] = currentCentroids[iterator];
        noOfPixels[iterator] = 0;
        noOfRedInCluster[iterator] = 0;
        noOfGreenInCluster[iterator] = 0;
        noOfBlueInCluster[iterator] = 0;
      }

      for ( int iterator = 0; iterator < pixels.length; iterator++ ) {
        maximumDistance = Double.MAX_VALUE;

        for ( int innerLooper = 0; innerLooper < currentCentroids.length; innerLooper++ ) {
          currentDistance = calculatePixelDistance( pixels[iterator], currentCentroids[innerLooper] );
          if ( currentDistance < maximumDistance ) {
            maximumDistance = currentDistance;
            closestCenter = innerLooper;
          }
        }

        clusterAssignment[iterator] = closestCenter;
        noOfPixels[closestCenter]++;
        noOfRedInCluster[closestCenter] +=   ((pixels[iterator] & 0x00FF0000) >>> 16);
        noOfGreenInCluster[closestCenter] += ((pixels[iterator] & 0x0000FF00) >>> 8);
        noOfBlueInCluster[closestCenter] +=  ((pixels[iterator] & 0x000000FF) >>> 0);
      }

      for ( int iterator = 0; iterator < currentCentroids.length; iterator++ ) {
        int averageOfRed =   (int)((double)noOfRedInCluster[iterator] /   (double)noOfPixels[iterator]);
        int averageOfGreen = (int)((double)noOfGreenInCluster[iterator] / (double)noOfPixels[iterator]);
        int averageOfBlue =  (int)((double)noOfBlueInCluster[iterator] /  (double)noOfPixels[iterator]);

        currentCentroids[iterator] = 
                            ((averageOfRed & 0x000000FF) << 16) |
                            ((averageOfGreen & 0x000000FF) << 8) |
                            ((averageOfBlue & 0x000000FF) << 0);
      }
    } while( !isConverged(previousCentroids, currentCentroids) );

    for ( int iterator = 0; iterator < pixels.length; iterator++ ) {
      pixels[iterator] = currentCentroids[clusterAssignment[iterator]];
    }
  }

  private static boolean isConverged( int[] previousCentroids, int[] currentCentroids ) {
    for ( int iterator = 0; iterator < currentCentroids.length; iterator++ )
      if ( previousCentroids[iterator] != currentCentroids[iterator] )
        return false;

    return true;
  }

  private static double calculatePixelDistance( int pixelA, int pixelB ) {
    int differenceOfRed = ((pixelA & 0x00FF0000) >>> 16) - ((pixelB & 0x00FF0000) >>> 16);
    int differenceOfGreen = ((pixelA & 0x0000FF00) >>> 8)  - ((pixelB & 0x0000FF00) >>> 8);
    int differenceOfBlue = ((pixelA & 0x000000FF) >>> 0)  - ((pixelB & 0x000000FF) >>> 0);
    return Math.sqrt( differenceOfRed*differenceOfRed + differenceOfGreen*differenceOfGreen + differenceOfBlue*differenceOfBlue );
  }
}