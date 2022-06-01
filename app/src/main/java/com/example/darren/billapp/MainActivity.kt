package com.example.darren.billapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.darren.billapp.ui.theme.BillAppTheme
import com.example.darren.billapp.components.InputField
import com.example.darren.billapp.widgets.RoundIconButton
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    BillAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}

@Composable
fun TopHeader(totalPer: MutableState<Double>){
    val totalAmountShowed = "%.2f".format(totalPer.value)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .padding(10.dp),
        color = Color(0xFFBFA1E4)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

        ) {
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "$$totalAmountShowed",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun MainContent(valueState: MutableState<String> = mutableStateOf("")){
    val result = remember {
        mutableStateOf(0.0)
    }
    Column {
        TopHeader(result)
        BillForm(){
            result.value = it
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValueChange: (Double) -> Unit = {}
)
{
    val billState = remember {
        mutableStateOf("")
    }
    val validState = remember(billState.value){
        billState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val splitAmount = remember {
        mutableStateOf(1)
    }
    val sliderState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderState.value * 100).roundToInt()
    val tipAmount = remember {
        mutableStateOf(0.0)
    }
    val totalPer = remember {
        mutableStateOf(0.0)
    }
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputField(valueState = billState,
                labelId = "Enter bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if(!validState){
                        return@KeyboardActions
                    }
                    keyboardController?.hide()
                    totalPer.value = (tipAmount.value + billState.value.toDouble())/ splitAmount.value
                    onValueChange(totalPer.value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            )
            if(validState){
                Row(
                    modifier = Modifier
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove) {
                            if (splitAmount.value > 1){
                                splitAmount.value--
                                totalPer.value = (tipAmount.value + billState.value.toDouble())/ splitAmount.value
                                onValueChange(totalPer.value)
                            }
                        }
                        Text(text = "${splitAmount.value}",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(horizontal = 2.dp),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                        RoundIconButton(imageVector = Icons.Default.Add) {
                            if(splitAmount.value < 100){
                                splitAmount.value++
                                totalPer.value = (tipAmount.value + billState.value.toDouble())/ splitAmount.value
                                onValueChange(totalPer.value)
                            }
                        }
                    }
                }
                Row(modifier = Modifier
                    .padding(6.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Tip  ",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "${tipAmount.value}",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "${tipPercentage}%"
                        , fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Slider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        value = sliderState.value,
                        onValueChange ={
                            sliderState.value = it
                            tipAmount.value =
                                calculateTipValue(
                                    billState.value.toDouble(),
                                    tipPercentage)
                            totalPer.value = (tipAmount.value + billState.value.toDouble())/ splitAmount.value
                            onValueChange(totalPer.value)
                        },
                        steps = 5
                    )

                }
                
            }
        }

    }
}

fun calculateTipValue(totalBill: Double, tipPercentage: Int): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty()){
        (totalBill * tipPercentage) / 100
    }else{
        0.0
    }
}


