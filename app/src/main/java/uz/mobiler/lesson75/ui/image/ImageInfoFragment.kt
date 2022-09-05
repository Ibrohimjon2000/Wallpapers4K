package uz.mobiler.lesson75.ui.image

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import uz.mobiler.lesson75.MainActivity
import uz.mobiler.lesson75.R
import uz.mobiler.lesson75.database.AppDatabase
import uz.mobiler.lesson75.database.entity.HitEntity
import uz.mobiler.lesson75.databinding.CustomDialogBinding
import uz.mobiler.lesson75.databinding.FragmentImageInfoBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as HitEntity?
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentImageInfoBinding
    private lateinit var wallpaperManager: WallpaperManager
    private lateinit var imageList: List<HitEntity>
    private lateinit var list: ArrayList<HitEntity>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var bol = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageInfoBinding.inflate(inflater, container, false)
        binding.apply {

            wallpaperManager = WallpaperManager.getInstance(requireContext())
            imageList = appDatabase.hitDao().getAllHits()
            sharedPreferences = requireContext().getSharedPreferences("Like", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
            val likeJsonString = sharedPreferences.getString("like", "")

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
                binding.download.visibility = View.VISIBLE
                binding.open.visibility = View.VISIBLE
                binding.edit.visibility = View.VISIBLE
                binding.like.visibility = View.VISIBLE

                binding.lockScreen.visibility = View.INVISIBLE
                binding.homeScreen.visibility = View.INVISIBLE
                binding.homeLock.visibility = View.INVISIBLE
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
                binding.download.visibility = View.VISIBLE
                binding.open.visibility = View.VISIBLE
                binding.edit.visibility = View.VISIBLE
                binding.like.visibility = View.VISIBLE

                binding.lockScreen.visibility = View.INVISIBLE
                binding.homeScreen.visibility = View.INVISIBLE
                binding.homeLock.visibility = View.INVISIBLE
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
                binding.download.visibility = View.VISIBLE
                binding.open.visibility = View.VISIBLE
                binding.edit.visibility = View.VISIBLE
                binding.like.visibility = View.VISIBLE

                binding.lockScreen.visibility = View.INVISIBLE
                binding.homeScreen.visibility = View.INVISIBLE
                binding.homeLock.visibility = View.INVISIBLE
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
                            saveImage()
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
                Toast.makeText(
                    requireActivity(),
                    "In progress",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.back.setOnClickListener {
                if (binding.lockScreen.visibility == View.VISIBLE) {
                    binding.share.visibility = View.VISIBLE
                    binding.about.visibility = View.VISIBLE
                    binding.download.visibility = View.VISIBLE
                    binding.open.visibility = View.VISIBLE
                    binding.edit.visibility = View.VISIBLE
                    binding.like.visibility = View.VISIBLE
                    binding.lockScreen.visibility = View.INVISIBLE
                    binding.homeScreen.visibility = View.INVISIBLE
                    binding.homeLock.visibility = View.INVISIBLE
                } else {
                    findNavController(requireView()).popBackStack()
                }
            }

            binding.open.setOnClickListener {
                binding.share.visibility = View.INVISIBLE
                binding.about.visibility = View.INVISIBLE
                binding.download.visibility = View.INVISIBLE
                binding.open.visibility = View.INVISIBLE
                binding.edit.visibility = View.INVISIBLE
                binding.like.visibility = View.INVISIBLE
                binding.lockScreen.visibility = View.VISIBLE
                binding.homeScreen.visibility = View.VISIBLE
                binding.homeLock.visibility = View.VISIBLE
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

    private fun saveImage() {
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
            val drawable = binding.img.drawable as BitmapDrawable
            val bitmap = drawable.bitmap

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
}