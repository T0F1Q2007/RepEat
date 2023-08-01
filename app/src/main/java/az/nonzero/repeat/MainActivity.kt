package az.nonzero.repeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import az.nonzero.repeat.datastore.StoredData
import az.nonzero.repeat.ui.theme.RepEatTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RepEatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Program()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Program() {
    // context
    val context = LocalContext.current
    // scope
    val scope = rememberCoroutineScope()
    // datastore Email
    val dataStore = StoredData(context)
    //High-score
    val high_score = dataStore.loadScore.collectAsState(initial = "")
    //Last question
    val last_question = dataStore.loadQues.collectAsState(initial = "")
    //
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var cavab by remember { mutableStateOf("") }
    var points by remember { mutableStateOf(0) }
    var numBer by remember { mutableStateOf(1) }
    val cavab_physics = physics_answer(last_question.value)
    var correction = when(cavab) {
        cavab_physics -> true
        else -> false
    }
    var snackbar_mes by remember { mutableStateOf("") }
    var permit_win by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        if(false) {
            FilledTonalButton(onClick = {
                null
            }
            ) {
                Icon(
                    Icons.Filled.List,
                    contentDescription = "List",
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize)
                )
            }
        }
        Text(
            text = "Ən yüksək: ${high_score.value}"+"          "+"Hazırki: $points",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            color = Color.Green
        )
        Spacer(modifier = Modifier.height(5.dp))
        Question_maker(last_question.value, modifier = Modifier)
        if(correction) {
            Text(
                text = "Cavab düzgündür!",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            )
        }
        Answer(
            label = R.string.field,
            value = cavab,
            ValueChange = { cavab = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        )
        Row {
            TextButton(
                onClick = {
                          if(permit_win) {permit_win=false}
                          else if(!permit_win) {permit_win=true}
                },
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Text(
                    text = "Sıfırla",
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.size(22.dp))
            TextButton(onClick = {
                snackbar_mes = "Tezliklə. .. "
            }) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Text(text = "Help", color = Color.LightGray)
            }
            TextButton(
                onClick = {
                    if (correction) {
                        if (cavab != "") {
                            points += 1
                        }
                        correction = false
                        snackbar_mes = ""
                        cavab = ""
                        numBer = (1..50).random()
                        if (points > (high_score.value.toString()).toInt()) {
                            scope.launch {
                                dataStore.editScore(points)
                            }
                        }
                        scope.launch {
                            dataStore.editQues(numBer)
                        }
                    } else if(cavab=="31") {
                        if((high_score.value.toString()).toInt()<31) {
                            scope.launch { dataStore.editScore(31) }
                        }
                    }

                },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)) {
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = stringResource(id = R.string.newS), color = Color.Cyan)
            }
        }
        if(permit_win) {
            FilledCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(10.dp),
                border = BorderStroke(1.dp, Color.Black),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "Ən yüksək xal sıfırlansın?", modifier = Modifier.padding(5.dp))
                    Row {
                        Spacer(modifier = Modifier.size(10.dp))
                        OutlinedButton(onClick = {
                            permit_win = false
                        }) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Cancel",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                        }
                        Spacer(modifier = Modifier.size(45.dp))
                        OutlinedButton(onClick = {
                            scope.launch {
                                dataStore.editScore(0)
                            }
                            permit_win = false
                        }
                        ) {
                            Icon(
                                Icons.Outlined.Done,
                                contentDescription = "Delete score",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                        }
                    }
                }
            }
        }
        //Bottom
        Row(modifier = Modifier
            .fillMaxHeight()
            .wrapContentHeight(Alignment.Bottom)) {
            if(snackbar_mes!="") {
                FilledCard(
                    border = BorderStroke(1.dp, Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(50.dp)
                ) {
                    Text(
                        text = snackbar_mes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun Question_maker(number: Any,modifier: Modifier) {
    val sual_physics = physics_question(number)
    Spacer(modifier = modifier.size(10.dp))
    Text(
        text = sual_physics,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center),
        fontSize = 24.sp,
        color = Color.DarkGray
    )
    Spacer(modifier = modifier.size(10.dp))
}

fun physics_question(num: Any): String{
    return when(num) {
        "1" -> "I hissə: \n v-v₀*t = ?"
        "2" -> "II hissə: \n q*E = ?"
        "3" -> "II hissə: \n N*e = ?"
        "4" -> "I hissə: \n F*S = ?"
        "5" -> "I hissə: \n (F*x)/2 = ?"
        "6" -> "I hissə \n r=6(m), v=12(m/san) \n a = ? m/san²"
        "7" -> "II hissə \n (q*E)/m = ?"
        "8" -> "II hissə \n q*v*B(*sinx) = ?"
        "9" -> "II hissə \n 1 Tl(Tesla) -> ?"
        "10" -> "II hissə \n (m*v)/(q*B) = ?"
        "11" -> "I hissə \n (2*π*R)/T = ?"
        "12" -> "I hissə \n (4*π²*R)/T² = ?"
        "13" -> "I hissə \n v²/R = ?"
        "14" -> "I hissə \n ⩟t=(1/2)*T \n v=? \n (məs.:a*b)"
        "15" -> "I hissə \n ⩟t=(1/6)*T \n Ek = ? C"
        "16" -> "mksan = 10ⁿ san \n n = ?"
        "17" -> "I hissə \n (v₀)²/(2*a) = ?"
        "18" -> "I hissə \n Ft=60(N), P=200(Pa) \n S = ? m²"
        "19" -> "I hissə \n t=2(dəq.), N=120(dövr) \n T = ? san"
        "20" -> "I hissə \n m=0.2(t), P(çəki) = ? N"
        "21" -> "I hissə \n µ*m*g = ?"
        "22" -> "I hissə \n a=4(m/san²), F=20(N) \n m = ? kq"
        "23" -> "II hissə \n q/U = ?"
        "24" -> "I hissə \n V=3(l), m=2.4(kq) \n ρ = ? kq/m³"
        "25" -> "II hissə \n (ε*ε₀*S)/d = ?"
        "26" -> "II hissə \n (ε*ε₀*E²*V)/2 = ?"
        "27" -> "II hissə \n (C*U²)/2 = ?"
        "28" -> "II hissə \n q²/(2*w) = ?"
        "29" -> "II hissə \n (q*U)/2 = ?"
        "30" -> "I hissə \n η = ((Q₁-Q₂)/X₁)*100% \n X = ?"
        "31" -> "I hissə \n c*m = ?"
        "32" -> "I hissə \n Q/m = ?"
        "33" -> "II hissə \n E*d = ?"
        "34" -> "II hissə \n (k*|q₀|)/R² = ?"
        "35" -> "II hissə \n (ρ*l)/S = ?"
        "36" -> "II hissə \n B=1.2(mTl), S=2(m²), ϕ=2.4(mVb) \n ∝ = ? \n (Səth ilə induksiya vektoru arasındakı)"
        "37" -> "I hissə \n (g*t²)/2 = ?"
        "38" -> "I hissə \n A/t = ?"
        "39" -> "I hissə \n µ*m*g*v = ?"
        "40" -> "I hissə \n a=32(m/san²), v=4(m/san) \n R = ? m"
        "41" -> "II hissə \n R=0.5(Om), N=6*10¹⁸, e=1.6*(1/10¹⁹) \n ⩟ϕ = ? Vb"
        "42" -> "II hissə \n ⩟ϕ/R = ?"
        "43" -> "II hissə \n I*t = ?"
        "44" -> "I hissə \n P(çəki)=200(mN) \n m = ? q"
        "45" -> "I hissə \n ϑ(tezlik)=50(1/san) \n T = ? san"
        "46" -> "II hissə \n 1 Hn -> ?"
        "47" -> "II hissə \n ϕ -> 1 ? \n (vahidi)"
        "48" -> "II hissə \n q/t = ?"
        "49" -> "II hissə \n q*ω = ? \n (q - amplitud qiyməti)"
        "50" -> "II hissə \n 1 Vb/Om = ?"
        else -> " "
    }
}
// π ² ∝ ⩟ µ ρ ν θ λ η δ φ ϑ ε ω ϕ
//Made by Tofiq
//nbefczupgjr
fun physics_answer(num: Any): String{
    return when(num) {
        "1" -> "a" //təcil
        "2" -> "F" //Qüvvə
        "3" -> "q" //yük
        "4" -> "A" //iş
        "5" -> "Ep" //Potensial enerji
        "6" -> "24"
        "7" -> "a" //təcil
        "8" -> "Fl" //Lorens qüvvəsi
        "9" -> "B" //induksiya
        "10" -> "R" //Radius
        "11" -> "v" //sürət
        "12" -> "a" //təcil
        "13" -> "a" //təcil
        "14" -> "2*v" //sürət
        "15" -> "0"
        "16" -> "-6"
        "17" -> "St" //Tormoz yolu
        "18" -> "0.3"
        "19" -> "1"
        "20" -> "2000"
        "21" -> "Fs" //Sürtünmə qüvvəsi
        "22" -> "5"
        "23" -> "C" //Elektrik tutumu
        "24" -> "800"
        "25" -> "C" //Elektrik tutumu
        "26" -> "w" //Enerji
        "27" -> "w" //Enerji
        "28" -> "C" //Elektrik tutumu
        "29" -> "w" //Enerji
        "30" -> "Q" //F.I.Ə.
        "31" -> "C" //istilik tutumu
        "32" -> "L" //Xüsusi buxarlanma istiliyi
        "33" -> "U" //Gərginlik
        "34" -> "E" //Intensivlik
        "35" -> "R" //Müqavimət
        "36" -> "90"
        "37" -> "h" //hündürlük
        "38" -> "N" //Güc
        "39" -> "N" //Güc
        "40" -> "0.5"
        "41" -> "0.48"
        "42" -> "q" //yük
        "43" -> "q" //yük
        "44" -> "20"
        "45" -> "0.02"
        "46" -> "L" //induktivlik
        "47" -> "Vb" //Veber
        "48" -> "I" //Cərəyan şiddəti
        "49" -> "I" //Cərəyan şiddəti
        "50" -> "Kl" //Kloun
        else -> ""
    }
}

@Composable
fun FilledCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke,
    content: @Composable ColumnScope.() -> Unit
) = OutlinedCard(
    modifier = modifier,
    shape = shape,
    colors = colors,
    elevation = elevation,
    border = border,
    content = content
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Answer(
    @StringRes label: Int,
    value: String,
    ValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = ValueChange,
        label = {
            Text(
                text = stringResource(id = label),
                modifier = Modifier.fillMaxWidth()
            )
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RepEatTheme {
        Program()
    }
}