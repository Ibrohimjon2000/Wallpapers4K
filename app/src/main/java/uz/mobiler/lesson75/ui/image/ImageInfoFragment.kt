package uz.mobiler.lesson75.ui.image

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.reflect.TypeToken
import com.permissionx.guolindev.PermissionX
import com.shashank.sony.fancytoastlib.FancyToast
import com.squareup.picasso.Picasso
import com.uvstudio.him.photofilterlibrary.PhotoFilter
import uz.mobiler.lesson75.MainActivity
import uz.mobiler.lesson75.R
import uz.mobiler.lesson75.adapters.FilterAdapter
import uz.mobiler.lesson75.database.AppDatabase
import uz.mobiler.lesson75.database.entity.HitEntity
import uz.mobiler.lesson75.databinding.CustomDialogBinding
import uz.mobiler.lesson75.databinding.FragmentImageInfoBinding
import uz.mobiler.lesson75.models.FilterModel
import uz.mobiler.lesson75.singleton.MyGson
import java.io.IOException
import java.util.*

private const val ARG_PARAM1 = "img"
private const val ARG_PARAM2 = "param2"

class ImageInfoFragment : Fragment() {
    val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance(requireContext())
    }

    private var param1: HitEntity? = null
    private var param2: String? = null
    private lateinit var binding: FragmentImageInfoBinding
    private lateinit var wallpaperManager: WallpaperManager
    private lateinit var imageList: List<HitEntity>
    private lateinit var list: ArrayList<HitEntity>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var filterList: ArrayList<FilterModel>
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var bitmapImage: Bitmap
    private var bol = 0
    private var isFilter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as HitEntity?
            param2 = it.getString(ARG_PARAM2)
        }
        loadBitmap()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageInfoBinding.inflate(inflater, container, false)
        binding.apply {
            val photoFilter = PhotoFilter()
            wallpaperManager = WallpaperManager.getInstance(requireContext())
            imageList = appDatabase.hitDao().getAllHits()
            sharedPreferences = requireContext().getSharedPreferences("Like", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
            val likeJsonString = sharedPreferences.getString("like", "")
            loadList()
            if (likeJsonString == "") {
                list = ArrayList()
            } else {
                val type = object : TypeToken<List<HitEntity?>?>() {}.type
                list = MyGson.getInstance().gson.fromJson(likeJsonString, type)
            }

            imageList.forEach {
                if (it.id == param1?.id) {
                    bol++
                }
            }
            if (bol == 0) {
                appDatabase.hitDao().addHitEntity(param1!!)
            }

            Glide.with(requireContext())
                .load(param1?.largeImageURL)
                .apply(
                    RequestOptions().centerCrop()
                )
                .into(img)

            if (list.isNotEmpty()) {
                for (i in list.indices) {
                    if (param1?.id == list[i].id) {
                        if (list[i].isLike) {
                            binding.editlike.setImageResource(R.drawable.ic_likebos)
                        } else {
                            binding.editlike.setImageResource(R.drawable.ic_like)
                        }
                        break
                    }
                }
            }

            filterAdapter =
                FilterAdapter(
                    requireContext(),
                    filterList,
                    object : FilterAdapter.OnItemClickListener {
                        override fun onItemClickListener(imageModel: FilterModel, position: Int) {
                            when (imageModel.name) {
                                "zero" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage = bitmap!!
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "one" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.one(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "two" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.two(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "three" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.three(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "four" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.four(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "five" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.five(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "six" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.six(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "seven" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.seven(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "eight" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.eight(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "nine" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.nine(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                                "ten" -> {
                                    Picasso.get()
                                        .load(param1?.largeImageURL)
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onBitmapLoaded(
                                                bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                                            ) {
                                                bitmapImage =
                                                    photoFilter.ten(requireContext(), bitmap)
                                                img.setImageBitmap(bitmapImage)
                                            }

                                            override fun onBitmapFailed(
                                                e: java.lang.Exception?,
                                                errorDrawable: Drawable?
                                            ) {

                                            }

                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                                            }
                                        })
                                    isFilter = true
                                }
                            }
                        }
                    })
            rv.adapter = filterAdapter

            binding.like.setOnClickListener {
                if (list.isEmpty()) {
                    list.add(param1!!)
                    if (!param1?.isLike!!) {
                        binding.editlike.setImageResource(R.drawable.ic_likebos)
                        list[0].isLike = true
                    }
                } else {
                    var bol = false
                    val size: Int = list.size
                    for (i in 0 until size) {
                        if (param1?.id == list[i].id) {
                            if (!list[i].isLike) {
                                binding.editlike.setImageResource(R.drawable.ic_likebos)
                                list[i].isLike = true
                            } else {
                                binding.editlike.setImageResource(R.drawable.ic_like)
                                list[i].isLike = false
                                list.removeAt(i)
                            }
                            bol = true
                            break
                        }
                    }
                    if (!bol) {
                        list.add(param1!!)
                        if (!param1?.isLike!!) {
                            binding.editlike.setImageResource(R.drawable.ic_likebos)
                            list[size].isLike = true
                        }
                    }
                }
                val jsonString = MyGson.getInstance().gson.toJson(list)
                editor.putString("like", jsonString).commit()
            }

            binding.homeScreen.setOnClickListener {
                Glide.with(requireContext()).asBitmap().load(param1?.largeImageURL)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Toast.makeText(
                                requireContext(),
                                "Fail to load image..",
                                Toast.LENGTH_SHORT
                            ).show()
                            return false
                        }

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            try {
                                wallpaperManager.setBitmap(
                                    resource,
                                    null,
                                    false,
                                    WallpaperManager.FLAG_SYSTEM
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    requireContext(),
                                    "Fail to set wallpaper",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return false
                        }
                    }).submit()
                FancyToast.makeText(
                    requireContext(),
                    "Wallpaper Set to Home Screen",
                    FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS,
                    false
                ).show()
                binding.share.visibility = View.VISIBLE
                binding.about.visibility = View.VISIBLE
                binding.liner.visibility = View.VISIBLE

                binding.liner3.visibility = View.GONE
            }

            binding.homeScreenFilter.setOnClickListener {
                Glide.with(requireContext()).asBitmap().load(bitmapImage)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Toast.makeText(
                                requireContext(),
                                "Fail to load image..",
                                Toast.LENGTH_SHORT
                            ).show()
                            return false
                        }

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            try {
                                wallpaperManager.setBitmap(
                                    resource,
                                    null,
                                    false,
                                    WallpaperManager.FLAG_SYSTEM
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    requireContext(),
                                    "Fail to set wallpaper",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return false
                        }
                    }).submit()
                FancyToast.makeText(
                    requireContext(),
                    "Wallpaper Set to Home Screen",
                    FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS,
                    false
                ).show()
                binding.liner2.visibility = View.VISIBLE
                binding.linerFilter.visibility = View.GONE
            }

            binding.lockScreen.setOnClickListener {
                Glide.with(requireContext()).asBitmap().load(param1?.largeImageURL)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Toast.makeText(
                                requireContext(),
                                "Fail to load image..",
                                Toast.LENGTH_SHORT
                            ).show()
                            return false
                        }

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            try {
                                wallpaperManager.setBitmap(
                                    resource,
                                    null,
                                    false,
                                    WallpaperManager.FLAG_LOCK
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    requireContext(),
                                    "Fail to set wallpaper",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return false
                        }
                    }).submit()
                FancyToast.makeText(
                    requireContext(),
                    "Wallpaper Set to Lock Screen",
                    FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS,
                    false
                ).show()
                binding.share.visibility = View.VISIBLE
                binding.about.visibility = View.VISIBLE
                binding.liner.visibility = View.VISIBLE

                binding.liner3.visibility = View.GONE
            }

            binding.lockScreenFilter.setOnClickListener {
                Glide.with(requireContext()).asBitmap().load(bitmapImage)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Toast.makeText(
                                requireContext(),
                                "Fail to load image..",
                                Toast.LENGTH_SHORT
                            ).show()
                            return false
                        }

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            try {
                                wallpaperManager.setBitmap(
                                    resource,
                                    null,
                                    false,
                                    WallpaperManager.FLAG_LOCK
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    requireContext(),
                                    "Fail to set wallpaper",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return false
                        }
                    }).submit()
                FancyToast.makeText(
                    requireContext(),
                    "Wallpaper Set to Lock Screen",
                    FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS,
                    false
                ).show()
                binding.liner2.visibility = View.VISIBLE
                binding.linerFilter.visibility = View.GONE
            }

            binding.homeLock.setOnClickListener {
                Glide.with(requireContext()).asBitmap().load(param1?.largeImageURL)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Toast.makeText(
                                requireContext(),
                                "Fail to load image..",
                                Toast.LENGTH_SHORT
                            ).show()
                            return false
                        }

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            try {
                                wallpaperManager.setBitmap(resource)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    requireContext(),
                                    "Fail to set wallpaper",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return false
                        }
                    }).submit()
                FancyToast.makeText(
                    requireContext(),
                    "Wallpaper Set to Home and Lock Screen",
                    FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS,
                    false
                ).show()
                binding.share.visibility = View.VISIBLE
                binding.about.visibility = View.VISIBLE
                binding.liner.visibility = View.VISIBLE

                binding.liner3.visibility = View.GONE
            }

            binding.homeLockFilter.setOnClickListener {
                Glide.with(requireContext()).asBitmap().load(bitmapImage)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Toast.makeText(
                                requireContext(),
                                "Fail to load image..",
                                Toast.LENGTH_SHORT
                            ).show()
                            return false
                        }

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            try {
                                wallpaperManager.setBitmap(resource)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    requireContext(),
                                    "Fail to set wallpaper",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return false
                        }
                    }).submit()
                FancyToast.makeText(
                    requireContext(),
                    "Wallpaper Set to Home and Lock Screen",
                    FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS,
                    false
                ).show()
                binding.liner2.visibility = View.VISIBLE
                binding.linerFilter.visibility = View.GONE
            }

            binding.download.setOnClickListener {
                PermissionX.init(activity)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(
                            deniedList,
                            "Core fundamental are based on these permissions",
                            "OK",
                            "Cancel"
                        )
                    }
                    .onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(
                            deniedList,
                            "You need to allow necessary permissions in Settings manually",
                            "OK",
                            "Cancel"
                        )
                    }
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            val drawable = binding.img.drawable as BitmapDrawable
                            val bitmap = drawable.bitmap
                            saveImage(bitmap)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "These permissions are denied: $deniedList",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }

            binding.edit.setOnClickListener {
                binding.share.visibility = View.GONE
                binding.about.visibility = View.GONE
                binding.back.visibility = View.GONE
                binding.liner.visibility = View.GONE
                binding.cencel.visibility = View.VISIBLE
                binding.check.visibility = View.VISIBLE
                binding.rv.visibility = View.VISIBLE
            }

            check.setOnClickListener {
                if (isFilter) {
                    binding.cencel.visibility = View.GONE
                    binding.check.visibility = View.GONE
                    binding.rv.visibility = View.GONE
                    binding.back.visibility = View.VISIBLE
                    binding.liner2.visibility = View.VISIBLE
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Filterlardan birini tanlang!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            openFilter.setOnClickListener {
                binding.back.visibility = View.VISIBLE
                binding.linerFilter.visibility = View.VISIBLE
                binding.liner2.visibility = View.GONE
            }

            downloadFilter.setOnClickListener {
                PermissionX.init(activity)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(
                            deniedList,
                            "Core fundamental are based on these permissions",
                            "OK",
                            "Cancel"
                        )
                    }
                    .onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(
                            deniedList,
                            "You need to allow necessary permissions in Settings manually",
                            "OK",
                            "Cancel"
                        )
                    }
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            saveImage(bitmapImage)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "These permissions are denied: $deniedList",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }

            cencel.setOnClickListener {
                binding.share.visibility = View.VISIBLE
                binding.about.visibility = View.VISIBLE
                binding.back.visibility = View.VISIBLE
                binding.liner.visibility = View.VISIBLE
                binding.cencel.visibility = View.GONE
                binding.check.visibility = View.GONE
                binding.rv.visibility = View.GONE
                Glide.with(requireContext())
                    .load(param1?.largeImageURL)
                    .apply(
                        RequestOptions().centerCrop()
                    )
                    .into(img)
                isFilter=false
            }

            binding.back.setOnClickListener {
                if (binding.liner3.visibility == View.VISIBLE) {
                    binding.share.visibility = View.VISIBLE
                    binding.about.visibility = View.VISIBLE
                    binding.liner.visibility = View.VISIBLE
                    binding.liner3.visibility = View.GONE
                } else if (binding.liner2.visibility == View.VISIBLE) {
                    binding.share.visibility = View.GONE
                    binding.about.visibility = View.GONE
                    binding.back.visibility = View.GONE
                    binding.liner.visibility = View.GONE
                    binding.cencel.visibility = View.VISIBLE
                    binding.check.visibility = View.VISIBLE
                    binding.rv.visibility = View.VISIBLE
                    binding.liner2.visibility = View.GONE
                } else if (binding.linerFilter.visibility == View.VISIBLE) {
                    binding.liner2.visibility = View.VISIBLE
                    binding.linerFilter.visibility = View.GONE
                } else {
                    findNavController(requireView()).popBackStack()
                }
            }

            binding.open.setOnClickListener {
                binding.share.visibility = View.GONE
                binding.about.visibility = View.GONE
                binding.liner.visibility = View.GONE
                binding.liner3.visibility = View.VISIBLE
            }

            binding.share.setOnClickListener {
                var share = Intent()
                share.action = Intent.ACTION_SEND
                share.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                share.putExtra(Intent.EXTRA_TEXT, param1?.pageURL)
                share.type = "text/plain"
                share = Intent.createChooser(share, "Share Via: ")
                requireContext().startActivity(share)
            }

            binding.about.setOnClickListener {
                val imgSize: Double = param1?.imageSize!! / 1048576.0
                val bottomSheetDialog = BottomSheetDialog(requireActivity())
                val customDialogBinding: CustomDialogBinding =
                    CustomDialogBinding.inflate(layoutInflater)
                customDialogBinding.website.text = "https://pixabay.com"
                customDialogBinding.author.text = param1?.user
                customDialogBinding.download.text = param1?.downloads.toString()
                customDialogBinding.size.text =
                    String.format(
                        "%.2f",
                        imgSize
                    ) + " MB  " + param1?.imageWidth + "x" + param1?.imageHeight
                bottomSheetDialog.setContentView(customDialogBinding.root)
                bottomSheetDialog.show()
            }

        }
        return binding.root
    }

    private fun loadBitmap() {
        Picasso.get()
            .load(param1?.largeImageURL)
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(
                    bitmap: Bitmap,
                    from: Picasso.LoadedFrom?
                ) {
                    bitmapImage = bitmap
                }

                override fun onBitmapFailed(
                    e: java.lang.Exception?,
                    errorDrawable: Drawable?
                ) {

                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }
            })
    }

    private fun saveImage(bitmap: Bitmap) {
        val images: Uri
        val contentResolver = context?.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            images = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.Images.Media.DISPLAY_NAME,
            System.currentTimeMillis().toString() + ".jpg"
        )
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*")
        val uri = contentResolver?.insert(images, contentValues)

        try {
            val openOutputStream = contentResolver?.openOutputStream(Objects.requireNonNull(uri)!!)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, openOutputStream)
            Objects.requireNonNull(openOutputStream)

            Toast.makeText(
                requireContext(),
                "Images Saved Successfully",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Images not Saved",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: HitEntity, param2: String) =
            ImageInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).show()
    }

    fun loadList() {
        filterList = ArrayList()
        filterList.add(FilterModel("zero", R.drawable.zero))
        filterList.add(FilterModel("one", R.drawable.one))
        filterList.add(FilterModel("two", R.drawable.two))
        filterList.add(FilterModel("three", R.drawable.three))
        filterList.add(FilterModel("four", R.drawable.four))
        filterList.add(FilterModel("five", R.drawable.five))
        filterList.add(FilterModel("six", R.drawable.six))
        filterList.add(FilterModel("seven", R.drawable.seven))
        filterList.add(FilterModel("eight", R.drawable.eight))
        filterList.add(FilterModel("nine", R.drawable.nine))
        filterList.add(FilterModel("ten", R.drawable.ten))
    }
}