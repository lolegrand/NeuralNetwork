import kotlin.math.exp
import kotlin.properties.Delegates
import kotlin.random.Random

fun stepFunction(x: Double) : Double =
    if (x >= 0) 1.0 else 0.0

fun sigmoid(x : Double) : Double =
    1 / (1 + exp(-x))

class Perceptron(
    val numberOfInput : Int
) {

    private val outputListener : MutableList<(Double) -> Unit> = mutableListOf()

    var output : Double by Delegates.observable(0.0) {
            _, _, newValue ->
        outputListener.forEach {
            it(newValue)
        }
    }
        private set

    private val input : MutableList<Double> = mutableListOf()

    private val weight : MutableList<Double> = mutableListOf()

    var bias = Random.nextDouble()

    init {
        for (i in 0 until numberOfInput) {
            input.add(0.0)
            weight.add(Random.nextDouble())
        }
    }

    fun addOutputListener(listener : (Double) -> Unit) {
        outputListener.add(listener)
    }

    private fun activationFunction() {
        output = stepFunction(this.weightedSum() - bias)
    }

    private fun weightedSum() : Double {
        var out = 0.0
        for (i in 0 until input.size)
            out += input[i] * weight[i]
        return out
    }

    fun connectEntryToOutput(inputId : Int, perceptron: Perceptron) {
        if (inputId < input.size && inputId >= 0) {
            perceptron.addOutputListener { this.input[inputId] = it }
        } else {
            throw RuntimeException("Connection out of bound")
        }
    }

    fun setInput(inputId : Int, value : Double) {
        if (inputId < input.size && inputId >= 0) {
            input[inputId] = value
        } else {
            throw RuntimeException("Set input out of bound")
        }
    }

    fun getWeight(weightId: Int) : Double {
        return weight[weightId]
    }

    fun getInput(inputId: Int) : Double {
        return input[inputId]
    }

    fun correctWeight(weightId : Int, newWeight: Double) {
        weight[weightId] = newWeight
    }

    fun removeInput(inputId: Int) {
        input.removeAt(inputId)
        weight.removeAt(inputId)
    }

    fun addInput(baseValue: Double?, baseWeight: Double?) {
        val v = baseValue ?: 0.0
        val w = baseWeight ?: Random.nextDouble()
        input.add(v)
        weight.add(w)
    }
    
    fun excite() {
        activationFunction()
    }
}

data class DataEntry(
    val inputs : List<Double>,
    val output : Double
)

class NeuralNetwork(
    private val trainingDataSet : List<DataEntry>,
    private val expectedAccuracy : Double
) {
    private val learningRate : Double = 1.0

    private val perceptron = Perceptron(2)

    fun startTraining() {
        var accuracy = 0.0
        while (accuracy < expectedAccuracy) {
            accuracy = 0.0
            trainingDataSet.forEach { dataEntry ->
                for (i in 0 until perceptron.numberOfInput) {
                    perceptron.setInput(i, dataEntry.inputs[i])
                }
                perceptron.excite()

                if (perceptron.output != dataEntry.output) {
                    println("\nErreur de prediction :")
                    print(" - Entrée : ")
                    dataEntry.inputs.forEach { print("$it ") }
                    println()
                    println(" - Sortie voulu : ${dataEntry.output}")
                    println(" - Sortie obtenue : ${perceptron.output}")

                    for (i in 0 until perceptron.numberOfInput) {
                        val correctedWeight =
                            perceptron.getWeight(i) + learningRate * (dataEntry.output - perceptron.output) * perceptron.getInput(i)
                        perceptron.correctWeight(i, correctedWeight)
                    }

                    perceptron.bias = perceptron.bias + learningRate * (dataEntry.output - perceptron.output)

                } else {
                    println("\nPrediction correct :")
                    print(" - Entrée : ")
                    dataEntry.inputs.forEach { print("$it ") }
                    println()
                    println(" - Sortie : ${dataEntry.output}")
                }

                accuracy += 1
            }
            accuracy /= trainingDataSet.size
            println("\n======End training accuracy $accuracy======")
        }
    }
}

fun main() {
    val dataEntry = mutableListOf(
        DataEntry(listOf(0.0, 0.0), 0.0),
        DataEntry(listOf(1.0, 0.0), 1.0),
        DataEntry(listOf(0.0, 1.0), 1.0),
        DataEntry(listOf(1.0, 1.0), 1.0),
    )

    val neuralNetwork = NeuralNetwork(dataEntry, 0.9)
    neuralNetwork.startTraining()
}
