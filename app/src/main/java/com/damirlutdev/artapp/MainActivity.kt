package com.damirlutdev.artapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.damirlutdev.artapp.model.Image
import com.damirlutdev.artapp.network.api.ApiRepository
import com.damirlutdev.artapp.ui.components.Skeleton
import com.damirlutdev.artapp.ui.theme.ArtAppTheme
import com.damirlutdev.artapp.ui.viewmodel.GalleryViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm = GalleryViewModel()

        setContent {
            ArtAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Page(vm)
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun Page(vm: GalleryViewModel) {
    val scope = rememberCoroutineScope()


    val ptrState =
        rememberPullRefreshState(vm.isLoading, { vm.getRandom() })

    LaunchedEffect(Unit, block = {
        scope.launch {
            vm.getRandom()
        }
    })

    if (vm.photos.isEmpty()) Text(text = "Loading...")

    Box(Modifier.pullRefresh(ptrState)) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(200.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize(),
            content = {
                items(vm.photos.size) { item ->
                    ImageCard(
                        image = vm.photos[item],
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
            }
        )
        PullRefreshIndicator(vm.isLoading, ptrState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun ImageCard(image: Image, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(image.path).crossfade(true).build(),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(4.dp)),
        loading = {
            Skeleton(modifier.height( 120.dp / image.ratio ))
        }
    )
}
