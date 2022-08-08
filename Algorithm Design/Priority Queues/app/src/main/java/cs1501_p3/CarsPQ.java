package cs1501_p3;


import java.util.*;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

public class CarsPQ implements CarsPQ_Inter
{
    private int[] pricepq;
    private int[] priceqp;
    private int[] milepq;
    private int[] mileqp;
    private Car[] cars;
    private int cursize;
    private int maxsize;

    public CarsPQ(String carsFile) 
    {
        maxsize = 100;
        cursize = 0;
        cars = new Car[maxsize];    // make this of length maxN??
        pricepq = new int[maxsize+1];
        priceqp = new int[maxsize];
        milepq = new int[maxsize+1];
        mileqp = new int[maxsize];                   // make this of length maxN??
        for (int i = 0; i < maxsize; i++)
        {
            priceqp[i] = -1;
            pricepq[i] = -1;
            mileqp[i] = -1;
            milepq[i] =-1;
        }

        try {
			Scanner myReader = new Scanner(new File(carsFile));
			while (myReader.hasNextLine())
			{
				String aline = myReader.nextLine();
				if(aline.isEmpty()||aline.startsWith("#")) continue;
				String[] configs = aline.split(":");
				Car car = new Car(configs[0],configs[1],configs[2],Integer.parseInt(configs[3]),Integer.parseInt(configs[4]),configs[5]);
				this.add(car);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("current dir" + System.getProperty("user.dir"));
			System.exit(1);
		}
    }

    //helper methods

    private void swimprice(int k) 
    {
        while (k > 1 && greaterprice(k/2, k)) 
        {
            exchprice(k, k/2);
            k = k/2;
        }
    }
    private void swimmile(int k) 
    {
        while (k > 1 && greatermile(k/2, k)) 
        {
            exchmile(k, k/2);
            k = k/2;
        }
    }
    private boolean greaterprice(int i, int j) 
    {
        return (cars[pricepq[i]].getPrice()>cars[pricepq[j]].getPrice());
    }

    private boolean greatermile(int i, int j) 
    {
        return (cars[milepq[i]].getMileage()>cars[milepq[j]].getMileage());
        
    }


    private void exchmile(int i, int j) 
    {
        int swap = milepq[i];
        milepq[i] = milepq[j];
        milepq[j] = swap;
        mileqp[milepq[i]] = i;
        mileqp[milepq[j]] = j;
    }
    private void exchprice(int i, int j) 
    {
        int swap = pricepq[i];
        pricepq[i] = pricepq[j];
        pricepq[j] = swap;
        priceqp[pricepq[i]] = i;
        priceqp[pricepq[j]] = j;
    }

    private void sinkmile(int k) 
    {
        while (2*k <= cursize) {
            int j = 2*k;
            if (j < cursize && greatermile(j, j+1)) j++;
            if (!greatermile(k, j)) break;
            exchmile(k, j);
            k = j;
        }
    }
    private void sinkprice(int k) 
    {
        while (2*k <= cursize) {
            int j = 2*k;
            if (j < cursize && greaterprice(j, j+1)) j++;
            if (!greaterprice(k, j)) break;
            exchprice(k, j);
            k = j;
        }
    }

    public int getIndex(String vin)
    {
        for(int i=0; i<maxsize; i++)
        {
            Car car = cars[i];
            if(car==null) continue;
            if(car.getVIN().equals(vin))
            {
                return i;
            }
        }
        return -1;

    }



    private int[] doubleSize(int[] array)
	{
		int[] newArray = new int[maxsize*2];
		for (int i = 0; i < maxsize; i++)
		{
			newArray[i] = array[i];
		}

		for (int j = maxsize; j < 2*maxsize; j++)
		{
			newArray[j] = -1;
		}
		return newArray;
	}

	private Car[] doubleSize(Car[] array)
	{
		Car[] newArray = new Car[maxsize*2];
		for (int i = 0; i < maxsize; i++)
		{
			newArray[i] = array[i];
		}
		return newArray;
	}

    private void doubleSizePQ()
	{
		cars = doubleSize(cars);
		milepq = doubleSize(milepq);
		mileqp = doubleSize(mileqp);
		pricepq = doubleSize(pricepq);
		priceqp = doubleSize(priceqp);
		maxsize = maxsize * 2;
	}

    private int firstAvailableSlot()
	{
		// first step is to find out any "empty slot" in cars[] that has null value
		for(int i =0; i<maxsize;i++)
		{
			if(cars[i] == null) return i;
		}
		return -1;
	}

    


	/**
	 * Add a new Car to the data structure
	 * Should throw an `IllegalStateException` if there is already car with the
	 * same VIN in the datastructure.
	 *
	 * @param 	c Car to be added to the data structure
	 */
	public void add(Car c) throws IllegalStateException
    {

        if(-1 == getIndex(c.getVIN()))
		{
			int ki = firstAvailableSlot();
			if(ki == -1)
			{
				ki = maxsize;
				doubleSizePQ();
			}
            //int tempIndex = cursize;
            cursize++;
            //if num is greater than maxN from global variables, we need to resize
            cars[ki] = c;
            pricepq[cursize] = ki;
			priceqp[ki] = cursize;
            milepq[cursize] = ki ; 
            mileqp[ki] = cursize;
            swimprice(cursize);
            swimmile(cursize);
		}
		else
		{
			throw new IllegalStateException("A car already exists with the specified VIN:"+c.getVIN());
		}
       
    
    }

	/**
	 * Retrieve a new Car from the data structure
	 * Should throw a `NoSuchElementException` if there is no car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be updated
	 */
	public Car get(String vin) throws NoSuchElementException
    {

        int carIndex = getIndex(vin);
		if( carIndex == -1)
		{
			throw new NoSuchElementException("No car with the specified VIN:" + vin);
		}
		else
		{
			return cars[carIndex];
		}
    }

	/**
	 * Update the price attribute of a given car
	 * Should throw a `NoSuchElementException` if there is no car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be updated
	 * @param	newPrice The updated price value
	 */
	public void updatePrice(String vin, int newPrice) throws NoSuchElementException
    {
        //acess old price of car from vin in car array

        Car c = get(vin);
		c.setPrice(newPrice);
		int carIndex = getIndex(vin);
		int nodeIndex = priceqp[carIndex];
		sinkprice(nodeIndex);
		swimprice(nodeIndex);

    }

	/**
	 * Update the mileage attribute of a given car
	 * Should throw a `NoSuchElementException` if there is not car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be updated
	 * @param	newMileage The updated mileage value
	 */
	public void updateMileage(String vin, int newMileage) throws NoSuchElementException
    {
        Car c = get(vin);
        c.setMileage(newMileage);
        int carIndex = getIndex(vin);
		int nodeIndex = mileqp[carIndex];
        sinkmile(nodeIndex);
        swimmile(nodeIndex);

    }

	/**
	 * Update the color attribute of a given car
	 * Should throw a `NoSuchElementException` if there is not car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be updated
	 * @param	newColor The updated color value
	 */
	public void updateColor(String vin, String newColor) throws NoSuchElementException
    {
        Car c = get(vin);
        c.setColor(newColor);   

    }

	/**
	 * Remove a car from the data structure
	 * Should throw a `NoSuchElementException` if there is not car with the 
	 * specified VIN in the datastructure.
	 *
	 * @param 	vin VIN number of the car to be removed
	 */
	public void remove(String vin) throws NoSuchElementException
    {

        boolean found = false;
		int ki = -1;
		for(ki=0;ki<maxsize;ki++)
		{
			if((cars[ki] != null) && (cars[ki].getVIN().equals(vin)))
			{
				found = true;
				break;
			}
		}
		if(found)
		{
			int mileageNodeIndex = mileqp[ki];
			int priceNodeIndex = priceqp[ki];
			exchmile(mileageNodeIndex,cursize); // swap the target node for delete with the last node
			exchprice(priceNodeIndex,cursize);
			cursize --;
			sinkmile(mileageNodeIndex);
			sinkprice(priceNodeIndex);
			swimprice(priceNodeIndex);
			swimmile(mileageNodeIndex);
			cars[ki] = null;
			mileqp[ki] = -1;
			priceqp[ki] = -1;
			pricepq[cursize+1] = -1;
			milepq[cursize+1] = -1;
		}
		else
		{
			throw new NoSuchElementException("No car found with VIN:"+vin);
		}

    }

	/**
	 * Get the lowest priced car (across all makes and models)
	 * Should return `null` if the data structure is empty
	 *
	 * @return	Car object representing the lowest priced car
	 */
	public Car getLowPrice()
    {
        return cars[pricepq[1]];

    }

	/**
	 * Get the lowest priced car of a given make and model
	 * Should return `null` if the data structure is empty
	 *
	 * @param	make The specified make
	 * @param	model The specified model
	 * 
	 * @return	Car object representing the lowest priced car
	 */
	public Car getLowPrice(String make, String model)
    {
        for(int i=1;i<=cursize;i++)
		{
			Car car = cars[pricepq[i]];
			if(car.getMake().equals(make) &&(car.getModel().equals(model)))
			{
				return car;
			}
		}
		return null;


    }

	/**
	 * Get the car with the lowest mileage (across all makes and models)
	 * Should return `null` if the data structure is empty
	 *
	 * @return	Car object representing the lowest mileage car
	 */
	public Car getLowMileage()
    {
        return cars[milepq[1]];

    }

	/**
	 * Get the car with the lowest mileage of a given make and model
	 * Should return `null` if the data structure is empty
	 *
	 * @param	make The specified make
	 * @param	model The specified model
	 *
	 * @return	Car object representing the lowest mileage car
	 */
	public Car getLowMileage(String make, String model)
    {
        for(int i=1;i<=cursize;i++)
		{
			Car car = cars[milepq[i]];
			if(car.getMake().equals(make) &&(car.getModel().equals(model)))
			{
				return car;
			}
		}
		return null;

    }
	

}


