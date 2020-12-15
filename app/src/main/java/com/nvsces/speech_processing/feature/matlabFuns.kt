package com.nvsces.speech_processing

import com.nvsces.speech_processing.feature.Complex
import com.nvsces.speech_processing.feature.FFTnew
import kotlin.math.*


fun min(a: Int, b: Int):Int{
    return if (a<b) a
    else b
}

fun pef(x: DoubleArray, fs: Int,optimisation:Boolean=false): ArrayList<Float> {

    val windowLength=Math.round(fs * 0.052)
    val overlapLength=Math.round(fs * (0.052-0.01))
    val Range= listOf<Float>(50f, 400f)
    val samplesPerChannel=x.size
    val numChannels=1 //
    val medianFilterLength=1

    val oneCast=1
    val r=x.size

    val hopLength=windowLength-overlapLength
    var numHopsFinal=Math.ceil(((r - windowLength) / hopLength).toDouble())+oneCast+1

    val N:Int=windowLength.toInt()
    val hopSize:Int= hopLength.toInt()

    var numHops:Int= (Math.ceil(((r - N) / hopSize).toDouble())+oneCast).toInt()+1
    if (optimisation){
        numHops=1
        numHopsFinal= 1.0
    }

    var y = Array(numHops) { FloatArray(N) }

    for (hop in 0 until numHops){
        val a=1+hopSize*(hop)
        val b=N+hopSize*(hop)
        val mbr=min(b, r)-a
        val temp: DoubleArray = DoubleArray(mbr)
        System.arraycopy(x, a, temp, 0, mbr)//assign temp

        y= assignArray(y, hop, min(N, mbr), temp)
    }

    val NFFT:Int= 2.0.pow(nextpow2(2 * windowLength.toFloat() - 1)).toInt()
    val nCol=y.size
    val logpred:Float= log10(kotlin.math.min(fs / 2 - 1, 4000).toDouble()).toFloat()
    val logSpacedFrequency= logspace(1f, logpred, NFFT)
    val linSpacedFrequency= linspace(
        0f,
        (fs / 2).toFloat(),
        (Math.round((NFFT / 2).toDouble()) + 1).toInt()
    )

    val wBandEdges=IntArray(2)
    wBandEdges[0]=0
    wBandEdges[1]=0



   for (i in 0 until Range.size){
       val mapLogSpacedFrequency=logSpacedFrequency.map { it-Range[i] }
       val absMapLogSpacedFrequency=mapLogSpacedFrequency.map { abs(it) }
       wBandEdges[i]=minIndex(absMapLogSpacedFrequency)
   }

    val edge=wBandEdges

    val bwTemp=ArrayList<Float>()

    for (i in 0 until logSpacedFrequency.size-2){
        bwTemp.add((logSpacedFrequency[i + 2] - logSpacedFrequency[i]) * 0.5f)
    }

    var bw=ArrayList<Float>()

    bw.add(bwTemp[0])
    bw.addAll(bwTemp)
    bw.add(bwTemp[bwTemp.size - 1])

    bw= bw.map { it/NFFT } as ArrayList<Float>

    val K=10
    val gamma=1.8
    val num:Int= round((logSpacedFrequency.size / 2).toDouble()).toInt()
    val q= logspace(log10(0.5).toFloat(), log10(K + 0.5).toFloat(), num)
    val h=q.map { 1/(gamma- cos(2 * PI * it)) }

    val d1=q[0]
    val d2=ArrayList<Float>()

    for (i in 0 until q.size-1){
        d2.add((q[i] + q[i + 1]) / 2)
    }
    val d3=q[q.size - 1]

    val dArray=ArrayList<Float>()
    dArray.add(d1)
    dArray.addAll(d2)
    dArray.add(d3)

    val delta= diff(dArray)

    val hdelta=multiplyArray(h, delta)

    val betta= sum(hdelta)/ sum(delta)

    val aFilt=h.map { it-betta }
    val numToPad= find_last_less(q, 1f)

    val win= hamming(y[0].size)

    //repmat
    val yw = Array(y.size) { FloatArray(y[0].size) }
    //val yw = Array(y.size) { FloatArray(NFFT) }

    for (i in 0 until y.size){
        yw[i]= multiplyArray(y[i], win)
    }

    //fft
    val YList=ArrayList<Array<Complex>>()

    for (i in 0 until yw.size){
        val yww=yw[i]
        val complex:Array<Complex> = createComplex(yw[i],NFFT)
        val Y= FFTnew.fft(complex)
        YList.add(Y)
    }


    val Yhalf=ArrayList<Array<Complex>>()

    for (i in 0 until YList.size){
        val tempArra=Array<Complex>(YList[i].size/2){ Complex(0.0,0.0) }
        System.arraycopy(YList[i],0,tempArra,0,YList[i].size/2)
        Yhalf.add(tempArra)
    }

    val YhalfConj=ArrayList<ArrayList<Complex>>()

    for (i in 0 until Yhalf.size){
        val listtemp=ArrayList<Complex>()
        for ( j in 0 until Yhalf[i].size){
            listtemp.add(YList[i][j].conjugate())
        }
        YhalfConj.add(listtemp)
    }

   val  Ypower=ArrayList<ArrayList<Double>>()

    for (i in 0 until Yhalf.size){

        val tet=ArrayList<Double>()
        for ( j in 0 until Yhalf[i].size){
            tet.add((Yhalf[i][j].times(YhalfConj[i][j])).re())
        }
        Ypower.add(tet)
    }

    val Ylog=ArrayList<FloatArray>()

    for ( i in 0 until Ypower.size){
        val tempYlog= interp1(linSpacedFrequency, Ypower[i], logSpacedFrequency)
        Ylog.add(multiplyArray(tempYlog, bw))
    }

        //stop
    val zer=ArrayList<FloatArray>()

    for (i in 0 until Ylog.size){
        val listZer=FloatArray(numToPad)
        for (j in 0 until numToPad){
            listZer[j]=0f
        }
        zer.add(listZer)
    }

    val Z=ArrayList<FloatArray>()

    for (i in 0 until zer.size){
        //val newZ=FloatArray(zer[i].size+Ylog[i].size)
        val newZ=zer[i]+Ylog[i]
        Z.add(newZ)
    }

    val mm= max(Z[0].size, aFilt.size)
    val mx1= min(wBandEdges[wBandEdges.size - 1], mm - 1)
    val m2= min(2.0.pow(nextpow2((2 * mm - 1).toFloat())).toInt(), NFFT * 4)

    val X=ArrayList<Array<Complex>>()

    for (i in 0 until Z.size){
        val tepZ= createComplex(Z[i],m2)
        val Xlist=FFTnew.fft(tepZ)
        X.add(Xlist)
    }
    // до этого момента правильно
    val Y=ArrayList<Complex>()
    val tempY= createComplex(doubleToFloat(aFilt),m2)
    val ylist=FFTnew.fft(tempY)

    for ( k in 0 until ylist.size){
        Y.add(ylist[k].conjugate())
    }
//до этого момента правильно
    val raz=ArrayList<Array<Complex>>()

    for ( i in 0 until X.size){
        val razlist=Array<Complex>(X[i].size){ Complex(0.0,0.0) }
        for (j in 0 until X[i].size){
            razlist[j]=(X[i][j].times(Y[j]))
        }
        raz.add(razlist)
    }

    val cl=ArrayList<DoubleArray>()

    for ( i in 0 until raz.size){
        val ct=FFTnew.ifft(raz[i])
        val ctReal=DoubleArray(ct.size)
        for ( icx in 0 until ct.size){
            ctReal[icx]=ct[icx].re()
        }
        cl.add(ctReal)
    }
    val lintemp= linspace(1, mx1, mx1)
    val alin= linspace(1,mx1-1,mx1-1)

    val atemp=IntArray(mx1-1)

    for (i in 0 until atemp.size){
        atemp[i]= (m2-mx1+alin[i])
    }

    val rR=ArrayList<DoubleArray>()
    for ( i in 0 until cl.size){
        val arrayA=DoubleArray(atemp.size)
        val arrayB=DoubleArray(lintemp.size)
        for (j in 0 until lintemp.size){
                if (j<atemp.size){
                    arrayA[j]=cl[i][atemp[j]]
                    arrayB[j]=cl[i][lintemp[j]+1]
                }else{
                    arrayB[j]=cl[i][lintemp[j]+1]
                }
        }
        val masrez=arrayA+arrayB
        rR.add(masrez)
    }

    val domain=ArrayList<DoubleArray>()

    for (i in 0 until rR.size){
        val nsize=rR[i].size-(wBandEdges[wBandEdges.size - 1]-1)
        val tempDomain=DoubleArray(nsize)
        for (j in 0 until nsize){
            tempDomain[j]=rR[i][wBandEdges[wBandEdges.size - 1]  + j-1]
        }
        domain.add(tempDomain)
    }

    val numCol=domain.size
    val numCandidates=1

    val locs=IntArray(numCol)
    val peaks=DoubleArray(numCol)

    val lower=wBandEdges[0]
    val upper=wBandEdges[wBandEdges.size - 1]

    for (c in 0 until numCol){
        val tempDomainR=DoubleArray(upper - lower)
        for (j in 0 until tempDomainR.size){
            tempDomainR[j]=domain[c][lower + j]
        }
        val rezMax= maxIndexAndValue(tempDomainR)
        locs[c]= rezMax[1].toInt()+lower-1
        peaks[c]=rezMax[0]
    }

    val f0=ArrayList<Float>()
    for ( i in 0 until locs.size){
        f0.add(logSpacedFrequency[locs[i]])
    }



    return f0
}

fun createComplex(floats: FloatArray, nfft: Int): Array<Complex> {

    val arra=Array<Complex>(nfft){ Complex(0.0,0.0) }
    for (i in 0 until floats.size){
        arra[i]= Complex(floats[i].toDouble(),0.0)
    }

    return arra
}

fun maxIndexAndValue(x: DoubleArray): DoubleArray {
    val rezult=DoubleArray(2)
    var tempValue=x[0]
    var temIndex=0
    for (i in 0 until x.size){
        if (x[i]>tempValue){
            tempValue=x[i]
            temIndex=i
        }
    }
    rezult[0]=tempValue
    rezult[1]= temIndex.toDouble()

    return rezult
}


fun interp1(linear: FloatArray, Y: List<Double>, logspace: FloatArray): FloatArray {
    val n=logspace.size
    val interArray=FloatArray(n)
    var m=1

    for (k in 0 until n){
        while (linear[m]<logspace[k]){
            m=m+1
        }
        interArray[k]= (Y[m - 1]+(Y[m]-Y[m - 1])*(logspace[k]-linear[m - 1])/(linear[m]-linear[m - 1])).toFloat()
    }

    return interArray
}

fun hamming(sizeWin: Int): FloatArray {
    val win=FloatArray(sizeWin)

    for (i in 0 until sizeWin){
        win[i]= (0.54- 0.46*cos(2 * PI * i / sizeWin)).toFloat()
    }

    return win
}

fun find_last_less(x: FloatArray, m: Float): Int {
    var temp=0
    for (i in 0 until x.size){
        if (x[i]<m){
            temp=i
        }
    }
    return temp
}

fun sum(x: FloatArray): Float {
    var summa=0f
    for (i in 0 until x.size){
        summa= summa+x[i]
    }
    return summa
}

fun multiplyArray(arrayList: FloatArray, bw: ArrayList<Float>): FloatArray {
    val rezult=FloatArray(bw.size)

    for (i in 0 until bw.size){
        rezult[i]= (arrayList[i]*bw[i])
    }

    return rezult
}

fun multiplyArray(arrayList: ArrayList<Double>, bw: ArrayList<Float>): ArrayList<Double> {
    val rezult=ArrayList<Double>()

    for (i in 0 until bw.size){
        rezult[i]= (arrayList[i]*bw[i])
    }

    return rezult
}

fun doubleToFloat(list: List<Double>):FloatArray{
    val outAr=FloatArray(list.size)

    for (i in 0 until outAr.size){
        outAr[i]=list[i].toFloat()
    }
    return outAr
}

fun multiplyArray(h: FloatArray, delta: FloatArray): FloatArray {

    val rezult=FloatArray(delta.size)

    for (i in 0 until rezult.size){
        rezult[i]= (h[i]*delta[i])
    }

    return rezult
}


fun multiplyArray(h: List<Double>, delta: FloatArray):FloatArray {

    val rezult=FloatArray(delta.size)

    for (i in 0 until rezult.size){
        rezult[i]= (h[i]*delta[i]).toFloat()
    }

    return rezult
}



fun diff(x: ArrayList<Float>): FloatArray {
    val outArray=FloatArray(x.size - 1)
    for (i in 0 until x.size-1){
        outArray[i]=x[i + 1]-x[i]
    }
    return outArray
}

fun minIndex(array: List<Float>): Int {
 var IndexMin=0
    var minValue=array[0]

    for (i in 1 until array.size){
        if (array[i]<minValue){
            minValue=array[i]
            IndexMin=i
        }
    }
    return IndexMin
}


fun logspace(x1: Float, x2: Float, n: Int): FloatArray {
    val helpMas= linspace(x1, x2, n)
    val outAr=FloatArray(n)
    val d:Double= 10.0

    for (i in 0 until n){
        outAr[i]= 10.0.pow(helpMas[i].toDouble()).toFloat()
    }
    return outAr
}

fun linspace(x1: Int, x2: Int, n: Int): IntArray {
    val n1=n-1
    val step=(x2-x1)/n1
    val outAr=IntArray(n)

    for (i in 0 until n){
        outAr[i]= (x1+step*i).toInt()
    }
    return outAr
}

fun linspace(x1: Float, x2: Float, n: Int): FloatArray {
    val n1=n-1
    val step=(x2-x1)/n1
    val outAr=FloatArray(n)

    for (i in 0 until n){
        outAr[i]=x1+step*i
    }
    return outAr
}
fun nextpow2(x: Float):Int{
    return Math.ceil(log2(x).toDouble()).toInt()
}

fun assignArray(y: Array<FloatArray>, b: Int, c: Int, temp: DoubleArray): Array<FloatArray> {

    for (i in 0 until c ){
        y[b][i]= temp[i].toFloat()
    }

    return y
}