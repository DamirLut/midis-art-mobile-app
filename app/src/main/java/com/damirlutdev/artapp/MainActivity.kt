package com.damirlutdev.artapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.damirlutdev.artapp.model.Image
import com.damirlutdev.artapp.ui.components.Skeleton
import com.damirlutdev.artapp.ui.theme.ArtAppTheme
import com.damirlutdev.artapp.ui.viewmodel.GalleryViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ArtAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}


@Composable
fun App() {
    val vm = GalleryViewModel()
    val photoVm = PhotoViewModel()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Page(vm = vm, onImageClick = {
                navController.navigate("photo/${it.id}")
            })
        }
        composable("photo/{id}", arguments = listOf(navArgument("id") {
            type = NavType.StringType
        })) {
            Card(it.arguments?.getString("id") ?: "", photoVm)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun Page(vm: GalleryViewModel, onImageClick: (photo: Image) -> Unit) {
    val scope = rememberCoroutineScope()

    val onScrollToEnd: () -> Unit = {
        vm.getRandom()
    }

    val listState = rememberLazyStaggeredGridState()

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
                            .clickable(onClick = { onImageClick(vm.photos[item]) })
                    )
                }
            }
        )
        PullRefreshIndicator(vm.isLoading, ptrState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun ImageCard(image: Image, modifier: Modifier = Modifier, path: String = image.thumbs.original) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(path)
            .crossfade(true).build(),
        contentScale = ContentScale.FillWidth,
        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(4.dp)),
        loading = {
            Skeleton(modifier.height(150.dp / image.ratio))
        }
    )
}


@Composable
fun Card(image_id: String, vm: PhotoViewModel) {

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit, block = {
        scope.launch {
            vm.getImage(image_id)
        }
    })

    Column(
        Modifier
            .background(Color.LightGray, RoundedCornerShape(16.dp)),
    ) {
        vm.photo?.let {
            ImageCard(
                image = it,
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                path = it.path
            )
        } ?: Skeleton()

    }
}