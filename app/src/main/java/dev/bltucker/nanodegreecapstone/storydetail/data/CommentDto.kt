package dev.bltucker.nanodegreecapstone.storydetail.data


class CommentDto(val by: String,
                 val id: Long,
                 val kids: LongArray,
                 val parent: Long,
                 val text: String,
                 val time: Long)
