package com.baran.techedout.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

import com.baran.techedout.container.GeneratorContainer;
import com.baran.techedout.tileentity.GeneratorTile;

import java.util.List;
import java.util.Random;

public class Generator extends Block {
	
	

    public Generator() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f)
                .setLightLevel((state) -> BStoL(state))
        );
        this.setDefaultState(this.getDefaultState().with(BlockStateProperties.POWERED, false));
    }
    
    private static Integer BStoL(BlockState state) {
    	if(state.get(BlockStateProperties.POWERED))
    		return 15;
    	else
    		return 0;
    }
    
    public void appendHoverText(ItemStack stack, IBlockReader reader, List<ITextComponent> list, ITooltipFlag flags) {
        list.add(new TranslationTextComponent("message.generator"));
    }
    
    public boolean hasTileEntity(BlockState state) {
    	return true;
    }
    
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    	return new GeneratorTile();
    }
    
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	return getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }
    
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof GeneratorTile) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen.techedout.generator");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new GeneratorContainer(i, world, pos, playerInventory, playerEntity);
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        }
        return ActionResultType.SUCCESS;
    }
    
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    	builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED);
    }
    
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(BlockStateProperties.POWERED)) {
           double d0 = (double)pos.getX() + 0.5D;
           double d1 = (double)pos.getY();
           double d2 = (double)pos.getZ() + 0.5D;

           Direction direction = stateIn.get(BlockStateProperties.HORIZONTAL_FACING);
           Direction.Axis direction$axis = direction.getAxis();
           double d3 = 0.52D;
           double d4 = rand.nextDouble() * 0.5D - 0.25D;
           double d5 = direction$axis == Direction.Axis.X ? (double)direction.getXOffset() * 0.52D : d4;
           double d6 = (rand.nextDouble() * 7.0D + 4.0D) / 16.0D;
           double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getZOffset() * 0.52D : d4;
           worldIn.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
           worldIn.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);

           worldIn.addParticle(ParticleTypes.SMOKE, d0 - d5, d1 + d6, d2 - d7, 0.0D, 0.0D, 0.0D);
           worldIn.addParticle(ParticleTypes.FLAME, d0 - d5, d1 + d6, d2 - d7, 0.0D, 0.0D, 0.0D);
           
           worldIn.addParticle(ParticleTypes.SMOKE, d0 + d7, d1 + d6, d2 + d5, 0.0D, 0.0D, 0.0D);
           worldIn.addParticle(ParticleTypes.FLAME, d0 + d7, d1 + d6, d2 + d5, 0.0D, 0.0D, 0.0D);

           worldIn.addParticle(ParticleTypes.SMOKE, d0 - d7, d1 + d6, d2 - d5, 0.0D, 0.0D, 0.0D);
           worldIn.addParticle(ParticleTypes.FLAME, d0 - d7, d1 + d6, d2 - d5, 0.0D, 0.0D, 0.0D);
        }
     }

}
